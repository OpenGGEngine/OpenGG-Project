/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.Resource;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.system.MemoryUtil;

/**
 * Static handler for loading and processing BMF Model files
 * @author Warren
 */
public class ModelLoader { 
    
    public static Model loadModel(String name){
        return ModelManager.loadModel(name);
    }
    
    /**
     * Creates a new Model object from the given path. Unless you know that a new object is needed, calls to {@link #loadModel(java.lang.String) loadModel()} should suffice<br>
     * Note: This method does not create the Drawable, so it is OpenGL safe
     * @param path Path of the model
     * @return Loaded model
     * @throws FileNotFoundException Throws if the model is not found
     * @throws IOException Throws if the file is inaccessible, fails to parse due to malformed lengths, or is invalidly formatted
     */
    public static Model forceLoadModel(String path) throws FileNotFoundException, IOException {
        
        GGConsole.log("Loading model at " + path + "...");
        
        String fpath = path;
            if(!new File(path).isAbsolute())
                fpath = Resource.getAbsoluteFromLocal(path);
        
        ArrayList<Mesh> meshes = new ArrayList<>();
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fpath)))) {
            String texpath = fpath.substring(0, fpath.lastIndexOf("\\") + 1) + "tex\\";
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

                int fam = in.readInt();
                int[] adjs = new int[fam * 3];
                for(int i = 0; i < fam * 3; i++){
                    adjs[i] = in.readInt();
                }
                
                int len = in.readInt();
                String name = "";
                for (int i = 0; i < len; i++) {
                    name += in.readChar();
                }
                
                Material m = new Material(name);
                if ("default".equals(name)) {
                    m = Material.defaultmaterial;
                }
                
                m.ka.x = in.readFloat();
                m.ka.y = in.readFloat();
                m.ka.z = in.readFloat();

                m.kd.x = in.readFloat();
                m.kd.y = in.readFloat();
                m.kd.z = in.readFloat();

                m.ks.x = in.readFloat();
                m.ks.y = in.readFloat();
                m.ks.z = in.readFloat();

                m.tf.x = in.readFloat();
                m.tf.y = in.readFloat();
                m.tf.z = in.readFloat();

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
                
                meshes.add(new Mesh(f, inb, m, adjs));
            }
        }
        GGConsole.log("Loaded model, generating any missing data...");
        Model m = new Model(path, meshes);
        GGConsole.log("Done Parsing " + path + ", got " + m.getName());
        return m;
    }
    
    public String readString(DataInputStream in) throws IOException{
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
