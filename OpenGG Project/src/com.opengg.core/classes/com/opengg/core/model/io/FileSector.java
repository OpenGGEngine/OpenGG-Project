package com.opengg.core.model.io;

import com.opengg.core.console.GGConsole;
import com.opengg.core.model.*;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.system.Allocator;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class FileSector {
    public ByteBuffer[] subBuffers;
    public SectorType type;

    public long length = 0;

    //Initialize Sector from Model
    public FileSector(Model model, SectorType type) throws IOException {
        switch(type){
            case VBO:
                genVBOSector(model);
            break;
            case IBO:
                genIBOSector(model);
            break;
            case BONES:
                genBonesSector(model);
                break;
            case MATERIAL:
                try {
                    genMatSector(model);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case NODES:
                genNodeSector(model);
                break;
            case ANIMATIONS:
                genAnimSector(model);
                break;
            case CONVEXHULL:
                genConvexHullSector(model);
        }
        this.type = type;
    }

    //Initialize Sector from BMF File
    public FileSector(SectorType type, int[] capacities, ByteBuffer original) throws IOException {
        this.type = type;
        this.subBuffers = new ByteBuffer[capacities.length];
        for(int i=0;i<this.subBuffers.length;i++){
            byte[] copy = new byte[capacities[i]];
            original.get(copy);
            ByteBuffer b = Allocator.alloc(capacities[i]).order(ByteOrder.BIG_ENDIAN);
            this.subBuffers[i] = b.put(copy).flip();
        }
    }
    private void genVBOSector(Model model){
        this.subBuffers = new ByteBuffer[model.getMeshes().size()];
        for(int i=0;i<model.getMeshes().size();i++){
            ByteBuffer sub = Allocator.alloc(model.getMeshes().get(i).getVbo().capacity()*Float.BYTES).order(ByteOrder.BIG_ENDIAN);
            model.getMeshes().get(i).getVbo().rewind();
            while(model.getMeshes().get(i).getVbo().hasRemaining())sub.putFloat(model.getMeshes().get(i).getVbo().get());
            this.subBuffers[i] = sub.flip();
            length += sub.limit();
        }
    }
    private void genIBOSector(Model model){
        this.subBuffers = new ByteBuffer[model.getMeshes().size()];
        for(int i=0;i<model.getMeshes().size();i++){
            ByteBuffer sub = Allocator.alloc(model.getMeshes().get(i).getIndexBuffer().capacity()*Integer.BYTES).order(ByteOrder.BIG_ENDIAN);
            model.getMeshes().get(i).getIndexBuffer().rewind();
            while(model.getMeshes().get(i).getIndexBuffer().hasRemaining()) sub.putInt(model.getMeshes().get(i).getIndexBuffer().get());
            this.subBuffers[i]= sub.flip();
            length += sub.limit();
        }
    }
    private void genBonesSector(Model model){
        this.subBuffers = new ByteBuffer[model.getMeshes().size()];
        length = 0;
        for(int i=0;i<model.getMeshes().size();i++){
            Mesh curmesh = model.getMeshes().get(i);
            if(curmesh.getBones() == null || !curmesh.genAnim){
                this.subBuffers[i] = ByteBuffer.allocate(0);
            }else{
                int boneNameSize = Stream.of(curmesh.getBones()).mapToInt(o->(4 +o.name.length())).sum();
                int bufferCap = Integer.BYTES + boneNameSize + (16 * Float.BYTES)* curmesh.getBones().length;
                length+=bufferCap;
                ByteBuffer boneBuffer = ByteBuffer.allocate(bufferCap);
                boneBuffer.putInt(curmesh.getBones().length);
                for(GGBone bone: curmesh.getBones()){
                    boneBuffer.putInt(bone.name.length());
                    boneBuffer.put(bone.name.getBytes(StandardCharsets.UTF_8));
                    MLoaderUtils.storeMat4(bone.offset,boneBuffer);
                }
                this.subBuffers[i] = boneBuffer.flip();
            }
        }
    }
    private void genMatSector(Model model) throws UnsupportedEncodingException {
        this.subBuffers = new ByteBuffer[model.getMaterials().size()+1];
        for(int i=0;i<this.subBuffers.length-1;i++) {
            this.subBuffers[i] = model.getMaterials().get(i).toBuffer(); length+=this.subBuffers[i].limit(); }
        this.subBuffers[this.subBuffers.length-1] = Allocator.alloc(Integer.BYTES*model.getMeshes().size()).order(ByteOrder.BIG_ENDIAN);
        for(int i=0;i<model.getMeshes().size();i++) this.subBuffers[this.subBuffers.length-1].putInt(model.getMeshes().get(i).matIndex);
        length+= this.subBuffers[this.subBuffers.length-1].flip().limit();

    }
    private void genNodeSector(Model model){
        if(model.getRootAnimationNode() == null)GGConsole.error("Node graph has not been initialized");
        length = model.getRootAnimationNode().byteSize;
        this.subBuffers = new ByteBuffer[]{Allocator.alloc(model.getRootAnimationNode().byteSize).order(ByteOrder.BIG_ENDIAN)};
        recurNodeStore(model.getRootAnimationNode(),this.subBuffers[0]);
        this.subBuffers[0].flip();
    }
    private void recurNodeStore(GGNode node, ByteBuffer b){
        MLoaderUtils.writeString(node.name,b);
        MLoaderUtils.storeMat4(node.transformation,b);
        b.putInt(node.children.size());
        for(GGNode c: node.children) recurNodeStore(c,b);
    }
    private void genAnimSector(Model model){
        this.subBuffers = new ByteBuffer[model.getAnimations().size()];
        int index =0; length = 0;
        for(GGAnimation anim:model.getAnimations().values()){
            subBuffers[index] = anim.toBuffer();
            length+=subBuffers[index].limit();
            index++;
        }

    }
    private void genConvexHullSector(Model model) throws IOException {
        this.subBuffers = new ByteBuffer[model.getMeshes().size()];
        int index =0; length = 0;
        for(Mesh mesh: model.getMeshes()){
            ByteBuffer output;
            if(mesh.getConvexHull() != null){
                GGOutputStream stream = new GGOutputStream();
                new ConvexHull(mesh.getConvexHull()).serialize(stream);
                byte[] b = stream.asByteArray();
                output = Allocator.alloc(b.length).put(b);
            }else{
                output = Allocator.alloc(4).putInt(0);
            }
            output.flip();
            length+= output.limit();
            subBuffers[index] = output;
            index++;
        }
    }

    public enum SectorType{
        VBO,IBO,BONES,MATERIAL,CONVEXHULL,NODES,ANIMATIONS
    }
}
