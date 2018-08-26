package com.opengg.core.model.ggmodel.io;

import com.opengg.core.model.ggmodel.GGBone;
import com.opengg.core.model.ggmodel.GGMesh;
import com.opengg.core.model.ggmodel.GGModel;
import com.opengg.core.system.Allocator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

public class FileSector {
    public ByteBuffer[] subBuffers;
    public SectorType type;

    //Initialize Sector from Model
    public FileSector(GGModel model, SectorType type){
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
        }
        this.type = type;
    }

    //Initialize Sector from BMF File
    public FileSector(SectorType type, int[] capacities, FileChannel fc) throws IOException {
        this.type = type;
        this.subBuffers = new ByteBuffer[capacities.length];
        for(int i=0;i<this.subBuffers.length;i++){
            ByteBuffer b = Allocator.alloc(capacities[i]);
            b.order(ByteOrder.BIG_ENDIAN);
            while(b.position()<capacities[i])fc.read(b);
            b.flip();
            this.subBuffers[i] = b;
        }
    }
    private void genVBOSector(GGModel model){
        this.subBuffers = new ByteBuffer[model.meshes.size()];
        for(int i=0;i<model.meshes.size();i++){
            ByteBuffer sub = ByteBuffer.allocateDirect(model.meshes.get(i).vbo.capacity()*Float.BYTES);
            sub.order(ByteOrder.BIG_ENDIAN);
            model.meshes.get(i).vbo.rewind();
            while(model.meshes.get(i).vbo.hasRemaining())sub.putFloat(model.meshes.get(i).vbo.get());
            sub.flip();
            this.subBuffers[i] = sub;
        }
    }
    private void genIBOSector(GGModel model){
        this.subBuffers = new ByteBuffer[model.meshes.size()];
        for(int i=0;i<model.meshes.size();i++){
            ByteBuffer sub = ByteBuffer.allocateDirect(model.meshes.get(i).ibo.capacity()*Integer.BYTES);
            sub.order(ByteOrder.BIG_ENDIAN);
            model.meshes.get(i).ibo.rewind();
            while(model.meshes.get(i).ibo.hasRemaining()) sub.putInt(model.meshes.get(i).ibo.get());
            sub.flip();
            this.subBuffers[i] = sub;
        }
    }
    private void genBonesSector(GGModel model){
        this.subBuffers = new ByteBuffer[model.meshes.size()];
        for(int i=0;i<model.meshes.size();i++){
            GGMesh curmesh = model.meshes.get(i);
            System.out.println(Arrays.toString(curmesh.bones));
            if(curmesh.bones == null || !curmesh.genAnim){
                this.subBuffers[i] = ByteBuffer.allocate(0);
            }else{
                int boneNameSize = Stream.of(curmesh.bones).mapToInt(o->o.name.length()).sum();
                int bufferCap = Integer.BYTES + boneNameSize + (Integer.BYTES  + 16 * Float.BYTES)*curmesh.bones.length;
                ByteBuffer boneBuffer = ByteBuffer.allocate(bufferCap);
                boneBuffer.putInt(curmesh.bones.length);
                for(GGBone bone:curmesh.bones){
                    boneBuffer.putInt(bone.name.length());
                    boneBuffer.put(bone.name.getBytes(StandardCharsets.UTF_8));
                    boneBuffer.putFloat(bone.offset.m00).putFloat(bone.offset.m01).putFloat(bone.offset.m02).putFloat(bone.offset.m03)
                            .putFloat(bone.offset.m10).putFloat(bone.offset.m11).putFloat(bone.offset.m12).putFloat(bone.offset.m13)
                            .putFloat(bone.offset.m20).putFloat(bone.offset.m21).putFloat(bone.offset.m22).putFloat(bone.offset.m23)
                            .putFloat(bone.offset.m30).putFloat(bone.offset.m31).putFloat(bone.offset.m32).putFloat(bone.offset.m33);
                }
                boneBuffer.flip();
                this.subBuffers[i] = boneBuffer;
            }
        }
    }
    private void genMatSector(GGModel model) throws UnsupportedEncodingException {
        this.subBuffers = new ByteBuffer[model.materials.size()+1];

        for(int i=0;i<this.subBuffers.length-1;i++)this.subBuffers[i] = model.materials.get(i).toBuffer();

        this.subBuffers[this.subBuffers.length-1] = ByteBuffer.allocate(Integer.BYTES*model.meshes.size());
        this.subBuffers[this.subBuffers.length-1].order(ByteOrder.BIG_ENDIAN);
        for(int i=0;i<model.meshes.size();i++) this.subBuffers[this.subBuffers.length-1].putInt(model.meshes.get(i).matIndex);
        this.subBuffers[this.subBuffers.length-1].flip();
    }

    public enum SectorType{
        VBO,IBO,BONES,MATERIAL,CONVEXHULL
    }
}
