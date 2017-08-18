/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.animation.AnimatedFrame;
import com.opengg.core.render.animation.Animation;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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

        String fpath = path;
        if (!new File(path).isAbsolute()) {
            fpath = Resource.getAbsoluteFromLocal(path);
        }
        ArrayList<Mesh> meshes = new ArrayList<>();
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fpath)))) {
            String texpath = fpath.substring(0, fpath.lastIndexOf("\\") + 1) + "tex\\";
            int veresion = in.readInt();
            boolean isanimated = in.readBoolean();
            int id = in.readInt();
            System.out.println("V: " + veresion + ", isAnim: " + isanimated + ", numMesh: " + id);
            for (int si = 0; si < id; si++) {
                int fbcap = in.readInt();
                // ByteBuffer f = MemoryUtil.memAlloc(fbcap*4);
                FloatBuffer f = MemoryUtil.memAllocFloat(fbcap);
                for (int i = 0; i < fbcap; i++) {

                    float e = in.readFloat();
                    f.put(e);

                }
                f.flip();
                int ibcap = in.readInt();
                System.out.println(ibcap);
                IntBuffer inb = MemoryUtil.memAllocInt(ibcap);
                for (int i = 0; i < ibcap; i++) {
                    inb.put(in.readInt());
                }
                inb.flip();

//                int fam = in.readInt();
//                int[] adjs = new int[fam * 3];
//                for(int i = 0; i < fam * 3; i++){
//                    adjs[i] = in.readInt();
//                }
                int len = in.readInt();
                String name = "";
                for (int i = 0; i < len; i++) {
                    name += in.readChar();
                }
                
                Material m;
                if ("default".equals(name)) {
                    m = Material.defaultmaterial;
                } else {
                    m = new Material(name, texpath, in);
                }
                m.loadTextures();
                meshes.add(new Mesh(f, inb, m, false));
            }
            if (isanimated) {
                int numanimations = in.readInt();
                System.out.println("sd: " + numanimations);
                for (int i = 0; i < numanimations; i++) {
                    ArrayList<AnimatedFrame> af = new ArrayList<>();
                    int stringlen = in.readInt();
                    String name = "";
                    for (int i2 = 0; i2 < stringlen; i2++) {
                        name += in.readChar();
                        System.out.println("num: "+i2+"," +name);
                    }
                    System.out.println(name);
                    double duration = in.readDouble();
                    int framecount = in.readInt();
                    for (int i2 = 0; i2 < framecount; i2++) {
                        
                        int matrixlength = in.readInt();
                        System.out.println("discon: "+matrixlength);
                        Matrix4f[] joints = new Matrix4f[matrixlength];
                        for (int i3 = 0; i3 < joints.length; i3++) {
                            float l00 = in.readFloat(); float l01 = in.readFloat(); float l02 = in.readFloat(); float l03 = in.readFloat();
                            float l10 = in.readFloat(); float l11= in.readFloat(); float l12= in.readFloat(); float l13= in.readFloat();
                            float l20= in.readFloat(); float l21= in.readFloat(); float l22= in.readFloat(); float l23= in.readFloat();
                            float l30= in.readFloat(); float l31= in.readFloat(); float l32= in.readFloat(); float l33= in.readFloat();
                            //ds.writeFloat(tes.m00);ds.writeFloat(tes.m10);ds.writeFloat(tes.m20);ds.writeFloat(tes.m30);
          //  ds.writeFloat(tes.m01);ds.writeFloat(tes.m11);ds.writeFloat(tes.m21);ds.writeFloat(tes.m31);
          //  ds.writeFloat(tes.m02);ds.writeFloat(tes.m12);ds.writeFloat(tes.m22);ds.writeFloat(tes.m32);
          //  ds.writeFloat(tes.m03);ds.writeFloat(tes.m13);ds.writeFloat(tes.m23);ds.writeFloat(tes.m33);
                            Matrix4f temp = new Matrix4f();
                            temp.m00 = l00;
                            temp.m10 = l01;
                            temp.m20 = l02;
                            temp.m30 = l03;
                            
                            temp.m01 = l10;
                            temp.m11 = l11;
                            temp.m21 = l12;
                            temp.m31 = l13;
                            
                            temp.m02 = l20;
                            temp.m12 = l21;
                            temp.m22 = l22;
                            temp.m32 = l23;
                            
                            temp.m03 = l30;
                            temp.m13 = l31;
                            temp.m23 = l32;
                            temp.m33 = l33;
                            
                            
                            
                            System.out.println("Matrix");
                            System.out.println(temp);
                            joints[i3] = temp;
                        }
                        AnimatedFrame am = new AnimatedFrame(joints);
                        af.add(am);
                    }
                    Animation anim = new Animation(name,af,duration);
                    anims.put(name, anim);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Model m = new Model(path, meshes);
        if(anims.size() > 0){
            m.animations = anims;
            m.isanimated = true;
        }
        System.out.println(m.toString());
        GGConsole.log("Done Parsing " + path + ", got " + m.getName());
        return m;
    }

    public String readString(DataInputStream in) throws IOException {
        int len = in.readInt();
        String string = "";
        if (len != 0) {
            for (int i = 0; i < len; i++) {
                string += in.readChar();
            }
        } else {
            string = "";
        }
        return string;
    }
}
