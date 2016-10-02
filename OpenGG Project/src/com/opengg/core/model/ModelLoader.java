/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.io.newobjloader.Material;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObjectGroup;
import com.opengg.core.render.drawn.MatDrawnObject;
import com.opengg.core.render.texture.Texture;
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

    public static Drawable loadModel(String path) throws FileNotFoundException, IOException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("C:/res/textbin.txt")));
        ArrayList<Drawable> obj = new ArrayList<>();
        int id =  in.readInt();
        for (int si = 0; si < id; si++) {
           // System.out.println("\r["+si +"/" +id +"]");
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

            MatDrawnObject test = new MatDrawnObject(f, inb);

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
                    m.mapKaFilename = name;
                } else {
                    m.mapKaFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.mapKdFilename = name;
                    Texture nointernet = new Texture();
                    nointernet.loadTexture("C:/res/" + "3DSMusicPark" + "/" + name, true);
                    test.setTexture(nointernet);
                } else {
                    m.mapKdFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.mapKsFilename = name;
                } else {
                    m.mapKsFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.mapNsFilename = name;
                    Texture nointernet = new Texture();
                    nointernet.loadTexture("C:/res/" + "3DSMusicPark" + "/" + name, true);
                    test.setSpecularMap(nointernet);
                } else {
                    m.mapNsFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.mapDFilename = name;
                } else {
                    m.mapDFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.decalFilename = name;
                } else {
                    m.decalFilename = null;
                }

                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.dispFilename = name;
                } else {
                    m.dispFilename = null;
                }
                
                len = in.readInt();
                if (len != 0) {
                    name = "";
                    for (int i = 0; i < len; i++) {
                        name += in.readChar();
                    }
                    m.bumpFilename = name;
                    Texture nointernet = new Texture();
                    nointernet.loadTexture("C:/res/" + "3DSMusicPark" + "/" + name, true);
                    test.setNormalMap(nointernet);
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
                    m.reflFilename = name;
                } else {
                    m.reflFilename = null;
                }
                obj.add(test);
            

        }
        in.close();
        print("Done Parsing");
        if (obj.size() > 1) {
            DrawnObjectGroup d = new DrawnObjectGroup(obj);
            return d;
        } else {
            return obj.get(0);
        }
    }
}
