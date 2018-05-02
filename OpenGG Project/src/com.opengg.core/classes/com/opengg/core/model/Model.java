/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector3f;
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
public class Model implements Resource{

    public Map<String, Animation> animations = new HashMap<>();
    private List<Mesh> meshes = new ArrayList<>();
    public ByteBuffer convexhull;
    public ArrayList<Vector3f> ch = new ArrayList<>();
    public String matlibname = "default";

    public boolean isanimated;
    public static int mversion = 1;
    public String source;
    private String name;
    public MaterialLibrary ml;

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

        ByteBuffer matlib = ByteBuffer.wrap(this.matlibname.getBytes(Charset.forName("UTF-8")));
        //matlib.flip();
//        matlib.rewind();
//        while(matlib.hasRemaining()){
//            System.out.println(matlib.get());
//        }
        System.out.println("Dig" + matlib.capacity());
        ByteBuffer header1 = ByteBuffer.allocate(16);

        header1.putInt(2);
        header1.putInt(isanimated ? 1 : 0);
        header1.putInt((meshes.size() * 3 * 4) + 4 + 4);
        header1.flip();

        ByteBuffer header = ByteBuffer.allocate((meshes.size() * 3 * 4) + 8);
        header.putInt(matlib.capacity());
        ByteBuffer[] buffers = new ByteBuffer[(meshes.size() * 3) + 4];
        buffers[0] = header1;
        buffers[1] = header;
        buffers[2] = matlib;

        MaterialLibrary ml = new MaterialLibrary();

        System.out.println("Mental:" + header.capacity());
        int pointer = 3;
        System.out.println("Total Meshes: " + meshes.size());
        for (Mesh mesh : meshes) {
            ByteBuffer fb = ByteBuffer.allocate(mesh.vbodata.capacity() * 4);
            fb.asFloatBuffer().put(mesh.vbodata);
            // fb.flip();
            buffers[pointer] = fb;
            header.putInt(fb.capacity());

            ByteBuffer fb1 = ByteBuffer.allocate(mesh.inddata.capacity() * 4);
            fb1.asIntBuffer().put(mesh.inddata);
            //    fb1.flip();
            buffers[pointer + 1] = fb1;
            header.putInt(fb1.capacity());

//             ByteBuffer fb2 = ByteBuffer.allocate(mesh.material.name.length());
//            fb2.put(mesh.material.name.getBytes(Charset.forName("UTF-8")));
//            //  fb2.flip();
//            buffers[pointer + 2] = fb2;
//             header.putInt(mesh.material.name.length());
            System.out.println("Real:" + mesh.material.name);
            System.out.println(new String(ByteBuffer.wrap(mesh.material.name.getBytes(Charset.forName("UTF-8"))).array(), Charset.forName("UTF-8")));
            ByteBuffer fb2 = ByteBuffer.wrap(mesh.material.name.getBytes(Charset.forName("UTF-8")));
            ml.mats.put(mesh.material.name, mesh.material);
            //  fb2.flip();
            buffers[pointer + 2] = fb2;
            header.putInt(fb2.capacity());

            pointer += 3;
        }
//        byte[] incredible = new byte[buffers[13].capacity()];
//        int wow = 0;
//        while(buffers[13].hasRemaining()){
//            
//            byte sd  = buffers[13].get();
//            incredible[wow] = sd;
//            wow ++;
//        }
        //    System.out.println("nobody:" + Arrays.toString(incredible));
        buffers[buffers.length - 1] = convexhull;
        header.putInt(convexhull.capacity());
        header.flip();

        FileOutputStream out = new FileOutputStream(file);
        ml.toFile(file.substring(0, file.length() - 4) + ".bml");
        GatheringByteChannel gather = out.getChannel();
        System.out.println("Buffers L :" + buffers.length);
        //  gather.write(Arrays.copyOfRange(buffers,0,buffers.length/2));
        //  gather.write(Arrays.copyOfRange(buffers,(buffers.length/2), buffers.length));
        for (int i = 0; i < buffers.length; i++) {
            System.out.println(buffers[i].capacity());
            gather.write(buffers[i]);

        }
        // System.out.println(out.getChannel().position());

        out.close();
//        while(header.hasRemaining()){
//            System.out.println(header.getInt());
//        }
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

    @Override
    public Type getType() {
        return Type.MODEL;
    }

    @Override
    public String getSource() {
        return source;
    }
}
