/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.util.GGInputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.system.MemoryUtil;

/**
 * Static handler for loading and processing BMF Model files
 *
 * @author Warren
 */
public class ModelLoader {

    public static Model loadModel(String name) {
        return ModelManager.loadModel(name);
    }

    /**
     * Creates a new Model object from the given path. Unless you know that a
     * new object is needed, calls to
     * {@link #loadModel(java.lang.String) loadModel()} should suffice<br>
     * Note: This method does not create the Drawable, so it is OpenGL safe
     *
     * @param path Path of the model
     * @return Loaded model
     * @throws FileNotFoundException Throws if the model is not found
     * @throws IOException Throws if the file is inaccessible, fails to parse
     * due to malformed lengths, or is invalidly formatted
     */
    public static Model forceLoadModel(String path) throws FileNotFoundException, IOException {
        Map<String, Animation> anims = new HashMap<>();
        GGConsole.log("Loading model at " + path + "...");

        ArrayList<Mesh> meshes = new ArrayList<>();
        GGInputStream in = new GGInputStream(new DataInputStream(new BufferedInputStream(new FileInputStream(path))));
        String texpath = path.substring(0, path.lastIndexOf(File.separator) + 1) + "tex" + File.separator;
        int version = in.readInt();
        boolean isanimated = in.readBoolean();
        int meshcount = in.readInt();
        for (int i = 0; i < meshcount; i++) {
            FloatBuffer vdata = in.readFloatBuffer();
            IntBuffer indices = in.readIntBuffer();

            String name = in.readString();

            Material m;
            if ("default".equals(name)) {
                m = Material.defaultmaterial;
            } else {
                m = new Material(name, texpath, in);
            }

            m.loadTextures();
            meshes.add(new Mesh(vdata, indices, m, false));
        }

        String test = in.readString();
        if (!(test.equals("animcheck"))) {
            throw new RuntimeException("Failed anti-corruption check!");
        }

        if (isanimated) {
            int numanimations = in.readInt();
            for (int i = 0; i < numanimations; i++) {
                ArrayList<AnimatedFrame> af = new ArrayList<>();
                String name = in.readString();
                float duration = in.readFloat();
                int framecount = in.readInt();

                for (int i2 = 0; i2 < framecount; i2++) {
                    int matrixlength = in.readInt();
                    Matrix4f[] joints = new Matrix4f[matrixlength];
                    for (int i3 = 0; i3 < joints.length; i3++) {
                        joints[i3] = in.readMatrix4f();
                    }

                    AnimatedFrame am = new AnimatedFrame(joints);
                    af.add(am);
                }

                Animation anim = new Animation(name, af, duration);
                anims.put(name, anim);
            }
        }

        Model model;

        if (isanimated) {
            model = new Model(path, meshes, anims);
        } else {
            model = new Model(path, meshes);
        }

        GGConsole.log("Done Parsing " + path + ", got " + model.getName());
        return model;
    }

    public static Model loadNewModel(String path) throws FileNotFoundException, IOException {
        FileInputStream in = new FileInputStream(path);

        ScatteringByteChannel scatter = in.getChannel();
        ByteBuffer headerlength = ByteBuffer.allocate(12);

        scatter.read(headerlength);

        headerlength.flip();
        
        int versionnumber = headerlength.getInt();
        int isanimated = headerlength.getInt();
        int headlen = headerlength.getInt();
        System.out.println("Loaded v "+versionnumber +" headlen "+ headlen );
        
        ArrayList<Mesh> meshes =  new ArrayList<>();
        
        ByteBuffer header = ByteBuffer.allocate(headlen);
        scatter.read(header);
        header.flip();
       //header.rewind();
        ByteBuffer[] arrays = new ByteBuffer[header.capacity()/4];
        int pointer = 0;
        while (header.hasRemaining()) {
            int header1s= header.getInt();
            arrays[pointer] = ByteBuffer.allocate(header1s);
            pointer++;
        }
        System.out.println("pointer:"+pointer);
        scatter.read(arrays);
        System.out.println("The purge: "+ arrays.length);
        for (int i = 0; i < arrays.length-1; i += 3) {
            FloatBuffer fbt = ((ByteBuffer)arrays[i].rewind()).asFloatBuffer();
                    System.out.println("cap: "+ fbt.limit() *4);
            System.out.println("---Entered Loop F---");
            
            FloatBuffer fb = MemoryUtil.memAllocFloat(fbt.limit());
            
           while(fbt.hasRemaining()){
               
               fb.put(fbt.get());
           }
            
         //  fb.rewind();
            fb.flip();
        
//       
             IntBuffer ibt = ((ByteBuffer)arrays[i+1] ).rewind().asIntBuffer();
             IntBuffer ib = MemoryUtil.memAllocInt(ibt.limit());
            
//            System.out.println("cap: "+ ib.capacity() );
//            System.out.println("---Entered Loop---");
           while(ibt.hasRemaining()){
                  ib.put(ibt.get());
//               System.out.println("flex : "+ib.get());
           }
            ib.flip();
   
            
            ByteBuffer name  = ((ByteBuffer)arrays[i+2] .rewind());
            name.flip();
            String name1 = new String(name.array(),Charset.forName("UTF-8"));
            System.out.println("Ectasydg:"+name1);
            System.out.println(fb.limit());
            Mesh m = new Mesh(fb,ib,Material.defaultmaterial,false);
            System.out.println("VBO " + i + ":" +arrays[i].capacity()/4);
            meshes.add(m);
        }
        Model m = new Model("Beer",meshes);
        m.convexhull = arrays[arrays.length-1];
                System.out.println("Model with m: "+m.getMeshes().size());
            return m;
        
    }
}
