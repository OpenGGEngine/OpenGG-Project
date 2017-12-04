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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        if(!(test.equals("animcheck")))
            throw new RuntimeException("Failed anti-corruption check!");
        
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
        }else{
            model = new Model(path, meshes);
        }
        
        GGConsole.log("Done Parsing " + path + ", got " + model.getName());
        return model;
    }
}
