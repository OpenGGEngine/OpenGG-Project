/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

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
    public ReflectivityTransmiss ka = new ReflectivityTransmiss();
    public ReflectivityTransmiss kd = new ReflectivityTransmiss();
    public ReflectivityTransmiss ks = new ReflectivityTransmiss();
    public ReflectivityTransmiss tf = new ReflectivityTransmiss();
    public int illumModel = 0;
    public boolean dHalo = false;
    public double dFactor = 0.0;
    public double nsExponent = 0.0;
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
    public Texture norm = null;
    
    public Material(String name) {
        this.name = name;
    }
    
    public void loadTextures(){
        if(mapKdFilename != null){
            Kd = new Texture(mapKdFilename);
        }
        if(mapKaFilename != null){
            Ka = new Texture(mapKaFilename);
        }
        if(mapKsFilename != null){
            Ks = new Texture(mapKdFilename);
        }
        if(bumpFilename != null){
            norm = new Texture(bumpFilename);
        }
    }
    
    public void toFileFormat(DataOutputStream s) throws IOException {
        s.writeInt(name.length());
        s.writeChars(name);
        s.writeDouble(ka.rx);
        s.writeDouble(ka.gy);
        s.writeDouble(ka.bz);
        s.writeDouble(kd.rx);
        s.writeDouble(kd.gy);
        s.writeDouble(kd.bz);
        s.writeDouble(ks.rx);
        s.writeDouble(ks.gy);
        s.writeDouble(ks.bz);
        s.writeDouble(tf.rx);
        s.writeDouble(tf.gy);
        s.writeDouble(tf.bz);
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
