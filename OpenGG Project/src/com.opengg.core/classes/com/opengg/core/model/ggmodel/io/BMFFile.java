package com.opengg.core.model.ggmodel.io;

import com.opengg.core.console.GGConsole;
import com.opengg.core.model.Material;
import com.opengg.core.model.ggmodel.GGMesh;
import com.opengg.core.model.ggmodel.GGModel;
import com.opengg.core.system.Allocator;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class BMFFile {
    private static final int VERSION = 1;
    private static final String HEADER_START = "OPENGG-BMF";

    public static void writeModel(GGModel model, String destination) throws IOException {
        FileOutputStream fOut = new FileOutputStream(new File(destination));
        FileChannel fc = fOut.getChannel();

        ArrayList<FileSector> sectors = new ArrayList<>();
        sectors.add(new FileSector(model,FileSector.SectorType.VBO));
        sectors.add(new FileSector(model,FileSector.SectorType.IBO));
        sectors.add(new FileSector(model,FileSector.SectorType.MATERIAL));

        fc.write(generateHeader(sectors));

        for(FileSector sector:sectors) {
            for(int i=0;i<sector.subBuffers.length;i++) {
                while(sector.subBuffers[i].hasRemaining()) {
                    fc.write(sector.subBuffers[i]);
                }
            }
        }
        fOut.close();
    }

    public static GGModel loadModel(String file) throws FileNotFoundException,IOException{
        File f = new File(file);
        FileInputStream fIn = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fIn);
        FileChannel fc = fIn.getChannel();
        //Weak Check for Header Validity
        for(int i=0;i<HEADER_START.length();i++){
            if(dis.readByte() != HEADER_START.charAt(i)){
                GGConsole.error("Invalid BMF Header");
                fIn.close();
                return null;
            }
        }
        int vNumber = dis.readInt();
        int numSector = dis.readInt();
        int[][] capacities = new int[numSector][];
        FileSector.SectorType[] types = new FileSector.SectorType[numSector];
        for(int sector = 0;sector<numSector;sector++){
            int numSubs = dis.readInt();
            capacities[sector] = new int[numSubs];
            types[sector] = FileSector.SectorType.values()[dis.readInt()];
            for(int i=0;i<numSubs;i++){
                capacities[sector][i] = dis.readInt();
            }
        }

        boolean hasAnim = false;
        ArrayList<GGMesh> meshes = new ArrayList<>();

        GGConsole.log("Loading BMF Model " + f.getName() + " with " + capacities[0].length + " meshes and " + numSector + " sectors.");
        //Load VBO and IBO
        FileSector fsVBO = new FileSector(types[0],capacities[0],fc);
        FileSector fsIBO = new FileSector(types[1],capacities[1],fc);

        for(int i=0;i<fsVBO.subBuffers.length;i++){
            //We dupe the buffers so the model renders
            FloatBuffer dupeFBBuf = Allocator.allocFloat(fsVBO.subBuffers[i].limit()/4);
            dupeFBBuf.put(fsVBO.subBuffers[i].rewind().asFloatBuffer());
            dupeFBBuf.flip();
            IntBuffer dupeIBBuf = Allocator.allocInt(fsIBO.subBuffers[i].limit()/4);
            dupeIBBuf.put(fsIBO.subBuffers[i].rewind().asIntBuffer());
            dupeIBBuf.flip();
            meshes.add(new GGMesh(dupeFBBuf,dupeIBBuf));
        }
        boolean isAnim = false;
        GGModel model = new GGModel(meshes);
        model.isAnim = isAnim;
        //Load Optional Sectors
        for(int i=2;i<numSector;i++){
            FileSector fs = new FileSector(types[i],capacities[i],fc);
            switch(fs.type){
                case MATERIAL:
                    ArrayList<Material> material = new ArrayList<Material>();
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
                    break;

            }
        }
        GGConsole.log("Loaded " + f.getName());
        return model;


    }

    public static ByteBuffer generateHeader(ArrayList<FileSector> sectors){
        //Header Contains: Version and OpenGG message, Number of sectors, SubSector sizes
        int headerSize = (HEADER_START.length())+ (Integer.BYTES*3) + (sectors.size()*Integer.BYTES *2) + (sectors.stream().mapToInt(s -> s.subBuffers.length).sum() * Integer.BYTES);
        ByteBuffer header = ByteBuffer.allocate(headerSize);

        header.put(HEADER_START.getBytes(StandardCharsets.UTF_8));
        header.putInt(VERSION);

        header.putInt(sectors.size());
        for(FileSector sector: sectors){
            header.putInt(sector.subBuffers.length);
            header.putInt(sector.type.ordinal());
            for(ByteBuffer temp:sector.subBuffers) header.putInt(temp.capacity());
        }

        header.flip();
        return header;
    }


}
