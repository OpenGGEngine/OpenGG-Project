/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.console.GGConsole;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.util.GGOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Warren
 */
public class Model {

    public Map<String, Animation> animations = new HashMap<>();
    private List<Mesh> meshes = new ArrayList<>();
    public ByteBuffer convexhull;

    public boolean isanimated;
    public static int mversion = 1;
    private String name;

    private ModelDrawnObject drawable = null;

    public Model(String name, List<Mesh> meshes) {
        this.name = name;
        this.meshes = meshes;
        this.isanimated = false;
    }

    public Model(String name, List<Mesh> meshes, Map<String, Animation> animations) {
        this.name = name;
        this.meshes = meshes;
        this.animations = animations;
        this.isanimated = true;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    private ModelDrawnObject generateDrawable() {
        GGConsole.log("Drawable for " + name + " has been requested, loading textures...");
        return new ModelDrawnObject(this);
    }

    public Drawable getDrawable() {
        if (drawable != null) {
            return drawable;
        }
        drawable = generateDrawable();
        return drawable;
    }

    public void putData(GGOutputStream out) throws IOException {
        GGConsole.log("Writing model data...");
        out.write(1);
        out.write(isanimated);

        out.write(meshes.size());
        for (Mesh mesh : meshes) {
            mesh.putData(out);
        }

        out.write("animcheck");

        if (isanimated) {
            out.write(animations.size());
            for (String s : animations.keySet()) {
                animations.get(s).writeBuffer(out);
            }
        }

        GGConsole.log("Finished putting data for " + name);
    }

    public void putDataNew(String file) throws IOException {
        GGConsole.log("Writing model data...");

        ByteBuffer header1 = ByteBuffer.allocate(12);
        header1.putInt(2);
        header1.putInt(isanimated ? 1 : 0);
        header1.putInt((meshes.size() * 3 * 4 ) + 4);
        header1.flip();
        
        ByteBuffer header = ByteBuffer.allocate((meshes.size() * 3 * 4 ) + 4);
        ByteBuffer[] buffers = new ByteBuffer[(meshes.size() * 3)+3];
        buffers[0] = header1;
        buffers[1] = header;
        System.out.println("Mental:"+header.capacity());
        int pointer = 2;
        System.out.println("Total Meshes: "+ meshes.size());
        for (Mesh mesh : meshes) {
            ByteBuffer fb = ByteBuffer.allocate(mesh.vbodata.capacity() * 4);
            fb.asFloatBuffer().put(mesh.vbodata);
        //    fb.flip();
            buffers[pointer] = fb;
            header.putInt(fb.capacity());
            
            ByteBuffer fb1 = ByteBuffer.allocate(mesh.inddata.capacity() * 4);
            fb1.asIntBuffer().put(mesh.inddata);
         //   fb1.flip();
            buffers[pointer + 1] = fb1;
              header.putInt(fb1.capacity());
              
              
//             ByteBuffer fb2 = ByteBuffer.allocate(mesh.material.name.length());
//            fb2.put(mesh.material.name.getBytes(Charset.forName("UTF-8")));
//            //  fb2.flip();
//            buffers[pointer + 2] = fb2;
//             header.putInt(mesh.material.name.length());
              
                ByteBuffer fb2 = ByteBuffer.wrap("afford".getBytes(Charset.forName("UTF-8")));
            //  fb2.flip();
            buffers[pointer + 2] = fb2;
             header.putInt(fb2.capacity());
             
            pointer += 3;
        }
        
        buffers[buffers.length-1] = convexhull;
        header.putInt(convexhull.capacity());
        header.flip();
        
        FileOutputStream out = new FileOutputStream(file);

        GatheringByteChannel gather = out.getChannel();
         gather.write(buffers);
         
         out.close();
//        out.write("animcheck");
//
//        if (isanimated) {
//            out.write(animations.size());
//            for (String s : animations.keySet()) {
//                animations.get(s).writeBuffer(out);
//            }
//        }

       
       

        GGConsole.log("Finished putting data for " + name);
    }

    public String getName() {
        return name;
    }
}
