package com.opengg.core.model.io;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.model.*;
import com.opengg.core.model.process.ModelProcess;
import com.opengg.core.system.Allocator;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.lz4.LZ4.*;
import static org.lwjgl.util.lz4.LZ4Frame.LZ4F_isError;

public class BMFFile extends ModelProcess {
    private static final int VERSION = 1;
    private static final String HEADER_START = "OPENGG-BMF";

    public void writeModel(Model model, String destination) throws IOException {
        FileOutputStream fOut = new FileOutputStream(new File(model.fileLocation + model.getName()+".bmf"));
        FileChannel fc = fOut.getChannel();

        ArrayList<FileSector> sectors = new ArrayList<>();
        sectors.add(new FileSector(model,FileSector.SectorType.VBO));
        sectors.add(new FileSector(model,FileSector.SectorType.IBO));
        sectors.add(new FileSector(model,FileSector.SectorType.MATERIAL));
        sectors.add(new FileSector(model,FileSector.SectorType.NODES));
        sectors.add(new FileSector(model,FileSector.SectorType.BONES));
        sectors.add(new FileSector(model,FileSector.SectorType.ANIMATIONS));

        ByteBuffer header = generateHeader(sectors);

        long filesize = sectors.stream().mapToLong(s->s.length).sum()+ header.limit();
        ByteBuffer uncompressed = Allocator.alloc((int)filesize);
        this.totaltasks = sectors.stream().mapToInt(s->s.subBuffers.length).sum();
        this.numcompleted = 0;
        uncompressed.put(header);
        for(FileSector sector:sectors) {
            for(int i=0;i<sector.subBuffers.length;i++) {
                while(sector.subBuffers[i].hasRemaining()) uncompressed.put(sector.subBuffers[i]);
                numcompleted++;
                broadcast();
            }
        }
        uncompressed.flip();

        //Compress the file
        ByteBuffer compressed = memAlloc(LZ4_compressBound(uncompressed.remaining()));
        long compressedSize = LZ4_compress_default(uncompressed, compressed);
        compressed.limit((int)compressedSize);
        compressed = compressed.slice();
        ByteBuffer sizeBuf = Allocator.alloc(4).order(ByteOrder.BIG_ENDIAN).putInt(uncompressed.capacity()).flip();
        fc.write(sizeBuf);
        fc.write(compressed);
        memFree(compressed);
        fOut.close();
        GGConsole.log("Exported Model: " + model.getName() + ".bmf at " + model.fileLocation);
    }

    public static Model loadModel(String file) throws IOException{
        String name = file;
        File f = new File(Resource.getAbsoluteFromLocal(name));
        FileInputStream fIn = new FileInputStream(f);
        //Get original file size from first 4 bytes.

        int originalsize = fIn.read()<< 24|(fIn.read()&0xFF)<<16|(fIn.read()&0xFF)<< 8|(fIn.read() & 0xFF);
        ByteBuffer original = memAlloc(originalsize).order(ByteOrder.BIG_ENDIAN);
        ByteBuffer compressed  = Allocator.alloc((int)f.length()-4);
        while(fIn.getChannel().read(compressed) > 0){}
        compressed.flip();
        long errorcode = LZ4_decompress_safe(compressed,original);
        if (LZ4F_isError(errorcode)){
            GGConsole.error("Decompression Failed: " + errorcode);
            fIn.close(); memFree(original); return null;
        }
        //Weak Check for Header Validity
        for(int i=0;i<HEADER_START.length();i++){
            if(original.get() != HEADER_START.charAt(i)){
                GGConsole.error("Invalid BMF Header");
                fIn.close();
                return null;
            }
        }
        int vNumber = original.getInt(); int numSector = original.getInt();
        int[][] capacities = new int[numSector][];
        FileSector.SectorType[] types = new FileSector.SectorType[numSector];
        for(int sector = 0;sector<numSector;sector++){
            int numSubs = original.getInt();
            capacities[sector] = new int[numSubs];
            types[sector] = FileSector.SectorType.values()[original.getInt()];
            for(int i=0;i<numSubs;i++){
                capacities[sector][i] = original.getInt();
            }
        }
        boolean hasAnim = false;

        GGConsole.log("Loading BMF Model " + f.getName() + " with " + capacities[0].length + " meshes and " + numSector + " sectors.");
        //Load VBO and IBO
        FileSector fsVBO = new FileSector(types[0],capacities[0],original);
        FileSector fsIBO = new FileSector(types[1],capacities[1],original);

        ArrayList<Mesh> meshes = new ArrayList<>(fsVBO.subBuffers.length);

        for(int i=0;i<fsVBO.subBuffers.length;i++){
            //We dupe the buffers so the model renders
            FloatBuffer dupeFBBuf = Allocator.allocFloat(fsVBO.subBuffers[i].limit()/4);
            dupeFBBuf.put(fsVBO.subBuffers[i].rewind().asFloatBuffer()).flip();
            IntBuffer dupeIBBuf = Allocator.allocInt(fsIBO.subBuffers[i].limit()/4);
            dupeIBBuf.put(fsIBO.subBuffers[i].rewind().asIntBuffer()).flip();
            meshes.add(new Mesh(dupeFBBuf,dupeIBBuf));
        }
        boolean isAnim = false;
        Model model = new Model(meshes, name);
        model.isAnim = isAnim;
        //Load Optional Sectors
        for(int i=2;i<numSector;i++){
            FileSector fs = new FileSector(types[i],capacities[i],original);
            switch(fs.type){
                case MATERIAL:
                    ArrayList<Material> material = new ArrayList<Material>(fs.subBuffers.length-1);
                    for(int i2=0;i2<fs.subBuffers.length-1;i2++) material.add(new Material(fs.subBuffers[i2]));
                    for(int i2=0;i2<meshes.size();i2++){
                        meshes.get(i2).matIndex = fs.subBuffers[fs.subBuffers.length-1].getInt();
                        meshes.get(i2).main = material.get(meshes.get(i2).matIndex);
                        meshes.get(i2).main.texpath = f.getParent()+"\\tex\\";
                    }
                    model.materials = material;
                    break;
                case BONES:
                    isAnim = true;
                    for(int i2 =0;i2<fs.subBuffers.length;i2++){
                        if(fs.subBuffers[i2].limit()==0){continue;}
                        int numbones = fs.subBuffers[i2].getInt();
                        GGBone[] bones = new GGBone[numbones];
                        for(int i3 = 0;i3<numbones;i3++){
                            bones[i3] = new GGBone(fs.subBuffers[i2]);
                        }
                        model.meshes.get(i2).bones = bones;
                    }
                    break;
                case NODES:
                    ByteBuffer data = fs.subBuffers[0];
                    model.root = recurNodeLoad(data);
                    break;
                case ANIMATIONS:
                    for(ByteBuffer b: fs.subBuffers){
                        GGAnimation anim = new GGAnimation(b);
                        model.animations.put(anim.name,anim);
                    }
                    break;
                    default:
                        GGConsole.warning("Unknown Sector: " + fs.type);
                        break;
            }
        }
        GGConsole.log("Loaded " + f.getName());
        return model;


    }

    public static ByteBuffer generateHeader(ArrayList<FileSector> sectors){
        //Header Contains: Version and OpenGG message, Number of sectors, SubSector sizes
        int headerSize = (HEADER_START.length())+ (Integer.BYTES*3) + (sectors.size()*Integer.BYTES *2) + (sectors.stream().mapToInt(s -> s.subBuffers.length).sum() * Integer.BYTES);
        ByteBuffer header = Allocator.alloc(headerSize).order(ByteOrder.BIG_ENDIAN);
        header.put(HEADER_START.getBytes(StandardCharsets.UTF_8)).putInt(VERSION).putInt(sectors.size());

        for(FileSector sector: sectors){
            header.putInt(sector.subBuffers.length).putInt(sector.type.ordinal());
            for(ByteBuffer temp:sector.subBuffers) header.putInt(temp.capacity());
        }
        return header.flip();
    }


    @Override
    public void process(Model model) {
        try {
            writeModel(model,model.fileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GGNode recurNodeLoad(ByteBuffer b){
        GGNode node = new GGNode(MLoaderUtils.readString(b), MLoaderUtils.loadMat4(b));
        int children = b.getInt();
        for(int i=0;i<children;i++) node.children.add(recurNodeLoad(b));
        return node;

    }
}
