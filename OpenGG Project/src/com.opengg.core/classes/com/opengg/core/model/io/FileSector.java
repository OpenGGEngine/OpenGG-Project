package com.opengg.core.model.io;

import com.opengg.core.console.GGConsole;
import com.opengg.core.model.*;
import com.opengg.core.system.Allocator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class FileSector {
    public ByteBuffer[] subBuffers;
    public SectorType type;

    public long length = 0;

    //Initialize Sector from Model
    public FileSector(Model model, SectorType type){
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
        this.subBuffers = new ByteBuffer[model.meshes.size()];
        for(int i=0;i<model.meshes.size();i++){
            ByteBuffer sub = Allocator.alloc(model.meshes.get(i).vbo.capacity()*Float.BYTES).order(ByteOrder.BIG_ENDIAN);
            model.meshes.get(i).vbo.rewind();
            while(model.meshes.get(i).vbo.hasRemaining())sub.putFloat(model.meshes.get(i).vbo.get());
            this.subBuffers[i] = sub.flip();
            length += sub.limit();
        }
    }
    private void genIBOSector(Model model){
        this.subBuffers = new ByteBuffer[model.meshes.size()];
        for(int i=0;i<model.meshes.size();i++){
            ByteBuffer sub = Allocator.alloc(model.meshes.get(i).ibo.capacity()*Integer.BYTES).order(ByteOrder.BIG_ENDIAN);
            model.meshes.get(i).ibo.rewind();
            while(model.meshes.get(i).ibo.hasRemaining()) sub.putInt(model.meshes.get(i).ibo.get());
            this.subBuffers[i]= sub.flip();
            length += sub.limit();
        }
    }
    private void genBonesSector(Model model){
        this.subBuffers = new ByteBuffer[model.meshes.size()];
        length = 0;
        for(int i=0;i<model.meshes.size();i++){
            Mesh curmesh = model.meshes.get(i);
            if(curmesh.bones == null || !curmesh.genAnim){
                this.subBuffers[i] = ByteBuffer.allocate(0);
            }else{
                int boneNameSize = Stream.of(curmesh.bones).mapToInt(o->(4 +o.name.length())).sum();
                int bufferCap = Integer.BYTES + boneNameSize + (16 * Float.BYTES)*curmesh.bones.length;
                length+=bufferCap;
                ByteBuffer boneBuffer = ByteBuffer.allocate(bufferCap);
                boneBuffer.putInt(curmesh.bones.length);
                for(GGBone bone:curmesh.bones){
                    boneBuffer.putInt(bone.name.length());
                    boneBuffer.put(bone.name.getBytes(StandardCharsets.UTF_8));
                    MLoaderUtils.storeMat4(bone.offset,boneBuffer);
                }
                this.subBuffers[i] = boneBuffer.flip();
            }
        }
    }
    private void genMatSector(Model model) throws UnsupportedEncodingException {
        this.subBuffers = new ByteBuffer[model.materials.size()+1];
        for(int i=0;i<this.subBuffers.length-1;i++) {
            this.subBuffers[i] = model.materials.get(i).toBuffer(); length+=this.subBuffers[i].limit(); }
        this.subBuffers[this.subBuffers.length-1] = Allocator.alloc(Integer.BYTES*model.meshes.size()).order(ByteOrder.BIG_ENDIAN);
        for(int i=0;i<model.meshes.size();i++) this.subBuffers[this.subBuffers.length-1].putInt(model.meshes.get(i).matIndex);
        length+= this.subBuffers[this.subBuffers.length-1].flip().limit();

    }
    private void genNodeSector(Model model){
        if(model.root == null)GGConsole.error("Node graph has not been initialized");
        length = model.root.byteSize;
        this.subBuffers = new ByteBuffer[]{Allocator.alloc(model.root.byteSize).order(ByteOrder.BIG_ENDIAN)};
        recurNodeStore(model.root,this.subBuffers[0]);
        this.subBuffers[0].flip();
    }
    private void recurNodeStore(GGNode node, ByteBuffer b){
        MLoaderUtils.writeString(node.name,b);
        MLoaderUtils.storeMat4(node.transformation,b);
        b.putInt(node.children.size());
        for(GGNode c: node.children) recurNodeStore(c,b);
    }
    private void genAnimSector(Model model){
        this.subBuffers = new ByteBuffer[model.animations.size()];
        int index =0; length = 0;
        for(GGAnimation anim:model.animations.values()){
            subBuffers[index] = anim.toBuffer();
            length+=subBuffers[index].limit();
            index++;
        }

    }

    public enum SectorType{
        VBO,IBO,BONES,MATERIAL,CONVEXHULL,NODES,ANIMATIONS
    }
}
