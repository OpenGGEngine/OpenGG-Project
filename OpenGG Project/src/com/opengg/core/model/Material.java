/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Texture;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Warren
 */
public class Material {
    
    public static Material defaultmaterial = new Material("default");
    public String name;
    public Vector3f ka = new Vector3f();
    public Vector3f kd = new Vector3f();
    public Vector3f ks = new Vector3f();
    public Vector3f tf = new Vector3f();
    public int illumModel = 0;
    public boolean dHalo = false;
    public double dFactor = 0.0;
    public double nsExponent = 16;
    public double sharpnessValue = 0.0;
    public double niOpticalDensity = 0.0;
    public String mapKaFilename = null;
    public String mapKdFilename = null;
    public String mapKsFilename = null;
    public String mapNsFilename = null;
    public String mapDFilename = null;
    public String decalFilename = null;
    public String dispFilename = null;
    public String bumpFilename = null;
    public int reflType = BuilderInterface.MTL_REFL_TYPE_UNKNOWN;
    public String reflFilename = null;
    public Texture Kd = null;
    public Texture Ka = null;
    public Texture Ks = null;
    public Texture Ns = null;
    public Texture D = null;
    public Texture norm = null;
    
    public boolean hasspecmap = false;
    public boolean hasnormmap = false;
    public boolean hasspecpow = false;
    public boolean hasreflmap = false;
    public boolean hastrans = false;
    
    public Material(String name) {
        this.name = name;
    }
    
    public void loadTextures(){
        if(mapKdFilename != null){
            Kd = Texture.get(mapKdFilename);
        }
        if(mapKaFilename != null){
            hasreflmap = true;
            Ka = Texture.get(mapKaFilename);
        }
        if(mapKsFilename != null){
            hasspecmap = true;
            Ks = Texture.get(mapKsFilename);
        }
        if(mapNsFilename != null){
            hasspecpow = true;
            Ns = Texture.get(mapNsFilename);
        }
        if(mapDFilename != null){
            hastrans = true;
            D = Texture.get(mapDFilename);
        }
        if(bumpFilename != null){
            hasnormmap = true;
            norm = Texture.get(bumpFilename);
        }
    }
    
    public void toFileFormat(DataOutputStream s) throws IOException {
        s.writeInt(name.length());
        s.writeChars(name);
        s.writeFloat(ka.x);
        s.writeFloat(ka.y);
        s.writeFloat(ka.z);
        s.writeFloat(kd.x);
        s.writeFloat(kd.y);
        s.writeFloat(kd.z);
        s.writeFloat(ks.x);
        s.writeFloat(ks.y);
        s.writeFloat(ks.z);
        s.writeFloat(tf.x);
        s.writeFloat(tf.y);
        s.writeFloat(tf.z);
        s.writeInt(illumModel);
        s.writeBoolean(dHalo);
        s.writeDouble(dFactor);
        s.writeDouble(nsExponent);
        s.writeDouble(sharpnessValue);
        s.writeDouble(niOpticalDensity);

        if (mapKaFilename != null) {
            s.writeInt(mapKaFilename.length());
            s.writeChars(mapKaFilename);
        } else {
            s.writeInt(0);
        }
        if (mapKdFilename != null) {
            s.writeInt(mapKdFilename.length());
            s.writeChars(mapKdFilename);
        } else {
            s.writeInt(0);
        }
        if (mapKsFilename != null) {
            s.writeInt(mapKsFilename.length());
            s.writeChars(mapKsFilename);
        } else {
            s.writeInt(0);
        }
        if (mapNsFilename != null) {
            s.writeInt(mapNsFilename.length());
            s.writeChars(mapNsFilename);
        } else {
            s.writeInt(0);
        }
        if (mapDFilename != null) {
            s.writeInt(mapDFilename.length());
            s.writeChars(mapDFilename);
        } else {
            s.writeInt(0);
        }
        if (decalFilename != null) {
            s.writeInt(decalFilename.length());
            s.writeChars(decalFilename);
        } else {
            s.writeInt(0);
        }
        if (dispFilename != null) {
            s.writeInt(dispFilename.length());
            s.writeChars(dispFilename);
        } else {
            s.writeInt(0);
        }
        if (bumpFilename != null) {
            s.writeInt(bumpFilename.length());
            s.writeChars(bumpFilename);
        } else {
            s.writeInt(0);
        }
        s.writeInt(reflType);
        if (reflFilename != null) {
            s.writeInt(reflFilename.length());
            s.writeChars(reflFilename);
        } else {
            s.writeInt(0);
        }

    }
}
