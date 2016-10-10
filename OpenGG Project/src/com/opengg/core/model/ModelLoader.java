/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import static com.opengg.core.util.FileUtil.getFileName;
import static com.opengg.core.util.GlobalUtil.print;
import static com.opengg.core.util.GlobalUtil.print;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Warren
 */
public class ModelLoader {

    public static Model loadModel(String path) throws FileNotFoundException, IOException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
        
        String texpath = path.substring(0, path.lastIndexOf("/") + 1) + "tex/";
        
        ArrayList<Mesh> meshes = new ArrayList<>();
        
        int id =  in.readInt();
        for (int si = 0; si < id; si++) {
            int fbcap = in.readInt();
            FloatBuffer f = MemoryUtil.memAllocFloat(fbcap);
            for (int i = 0; i < fbcap; i++) 
            {
                f.put(in.readFloat());
            }
            f.flip();
            int ibcap = in.readInt();
            IntBuffer inb = MemoryUtil.memAllocInt(ibcap);
            for (int i = 0; i < ibcap; i++) {
                inb.put(in.readInt());
            }
            inb.flip();

            int len = in.readInt();
            String name = "";
            for (int i = 0; i < len; i++) {
                name += in.readChar();
            }

            Material m = new Material(name);
            if ("default".equals(name)) {
                m = Material.defaultmaterial;
            } 

                m.ka.rx = in.readDouble();
                m.ka.gy = in.readDouble();
                m.ka.bz = in.readDouble();

                m.kd.rx = in.readDouble();
                m.kd.gy = in.readDouble();
                m.kd.bz = in.readDouble();

                m.ks.rx = in.readDouble();
                m.ks.gy = in.readDouble();
                m.ks.bz = in.readDouble();

                m.tf.rx = in.readDouble();
                m.tf.gy = in.readDouble();
                m.tf.bz = in.readDouble();

                m.illumModel = in.readInt();

                m.dHalo = in.readBoolean();

                m.dFactor = in.readDouble();

                m.nsExponent = in.readDouble();

                m.sharpnessValue = in.readDouble();

                m.niOpticalDensity = in.readDouble();

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.mapKaFilename = texpath + name;
                } else {
                    m.mapKaFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.mapKdFilename = texpath + name;
                } else {
                    m.mapKdFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.mapKsFilename = texpath + name;
                } else {
                    m.mapKsFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.mapNsFilename = texpath + name;
                } else {
                    m.mapNsFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.mapDFilename = texpath + name;
                } else {
                    m.mapDFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.decalFilename = texpath + name;
                } else {
                    m.decalFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.dispFilename = texpath + name;
                } else {
                    m.dispFilename = null;
                }
                
                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.bumpFilename = texpath + name;
                } else {
                    m.bumpFilename = null;
                }

                m.reflType = in.readInt();

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.reflFilename = texpath + name;
                } else {
                    m.reflFilename = null;
                }
           meshes.add(new Mesh(f, inb, m));
        }

        in.close();
        print("Done Parsing");
        return new Model(getFileName(path), meshes);
    }
}
