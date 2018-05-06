/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.system.Allocator;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Static handler for loading and processing BMF Model files
 *
 * @author Warren
 */
public class ModelLoader {
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
    static Model loadModel(String path) throws FileNotFoundException, IOException {
        Map<String, Animation> anims = new HashMap<>();
        GGConsole.log("Loading model at " + path + "...");

        ArrayList<Mesh> meshes = new ArrayList<>();
        GGInputStream in = new GGInputStream(new DataInputStream(new BufferedInputStream(new FileInputStream(Resource.getAbsoluteFromLocal(path)))));
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
        FileInputStream in = new FileInputStream(Resource.getAbsoluteFromLocal(path));

        ScatteringByteChannel scatter = in.getChannel();
        ByteBuffer headerlength = ByteBuffer.allocate(12);

        scatter.read(headerlength);

        headerlength.flip();

        int versionnumber = headerlength.getInt();
        int isanimated = headerlength.getInt();
        int headlen = headerlength.getInt();
        System.out.println("Loaded v " + versionnumber + " headlen " + headlen);
        String texpath = path.substring(0, path.lastIndexOf(File.separator) + 1) + "tex" + File.separator;

        ArrayList<Mesh> meshes = new ArrayList<>();

        MaterialLibrary ml = new MaterialLibrary(path.substring(0, path.length() - 4) + ".bml");

        ByteBuffer header = ByteBuffer.allocate(headlen);
        scatter.read(header);
        header.flip();
        //header.rewind();
        ByteBuffer[] arrays = new ByteBuffer[header.capacity() / 4];
        System.out.println("Meshsize:" + header.capacity() / 4);
        int pointer = 0;
        while (header.hasRemaining()) {
            int header1s = header.getInt();
            arrays[pointer] = ByteBuffer.allocate(header1s);
            scatter.read(arrays[pointer]);
            pointer++;
        }

       // scatter.read(arrays);

        ByteBuffer name3 = arrays[0];
        //name3.flip();
        //   arrays[0].flip();

        for (int i = 0; i < 7; i++) {
            System.out.println(arrays[0].get(i));
        }
        String matname = new String(name3.array(), Charset.forName("UTF-8"));
        //    System.out.println("Matname:" + matname);
        //    System.out.println("The purge: " + arrays.length);
        for (int i = 1; i < arrays.length - 1; i += 3) {
            FloatBuffer fbt = arrays[i].rewind().asFloatBuffer();
            // System.out.println("cap: " + fbt.limit() * 4);
            //  System.out.println("---Entered Loop F---");

            FloatBuffer fb = Allocator.allocFloat(fbt.limit());
            float[] fbg = new float[fb.capacity()];
            int died = 0;
            while (fbt.hasRemaining()) {
                        float dede = fbt.get();
                fb.put(dede);
                fbg[died] = dede;
                died++;
            }

            //  fb.rewind();
            fb.flip();
          //  System.out.println("Float: "+ Arrays.toString(fbg));
//       
            IntBuffer ibt = arrays[i + 1].rewind().asIntBuffer();
            IntBuffer ib = Allocator.allocInt(ibt.limit());

//            System.out.println("cap: "+ ib.capacity() );
//            System.out.println("---Entered Loop---");
            int[] bye = new int[ib.capacity()];
            
            int deadanimal = 0;
            while (ibt.hasRemaining()) {
                int young = ibt.get();
                ib.put(young);
                bye[deadanimal] = young;
                deadanimal++;
//               System.out.println("flex : "+ib.get());
            }
            ib.flip();
         //   System.out.println("Integer: "+ Arrays.toString(bye));

            ByteBuffer name = arrays[i + 2].rewind();
            System.out.println("Limit " +name.limit());
            System.out.println("Man: " +name.position());
            name.flip();
            String name1 = new String(name.array(), Charset.forName("UTF-8"));
            Material m3;
            System.out.println("Damien: " + Arrays.toString(name.array()));
            if (ml.mats.get(name1) != null) {
                m3 = ml.mats.get(name1);
            } else {
                //m3 = ml.mats.get(name1);
                m3 = Material.defaultmaterial;
            }
            m3.texpath = texpath;
            m3.loadTextures();
            Mesh m = new Mesh(fb, ib, m3, false);

            // System.out.println("VBO " + i + ":" + arrays[i].capacity() / 4);
            meshes.add(m);
        }
        Model m = new Model("Beer", meshes);
        System.out.println("Total Meshes: "+m.getMeshes().size());
        m.convexhull = arrays[arrays.length - 1];
        m.convexhull.rewind();
        while(m.convexhull.hasRemaining()){
            Vector3f wow = new Vector3f(m.convexhull.getFloat(), m.convexhull.getFloat(), m.convexhull.getFloat());
            m.ch.add(wow);
            System.out.println(wow.toString());
        }
        m.ml = ml;

        System.out.println("Model with matrix: " + m.getMeshes().size());
        return m;

    }
}
