/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Warren
 */
public class Material {
    
    public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static Material defaultmaterial = new Material("default");
    public String name;
    public Vector3f ka = new Vector3f();
    public Vector3f kd = new Vector3f();
    public Vector3f ks = new Vector3f();
    public Vector3f tf = new Vector3f();
    public int illumModel = 0;
    public boolean dHalo = false;
    public double dFactor = 0.0;
    public double nsExponent = 32;
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
    public String texpath = "";

    public boolean hasspecmap = false;
    public boolean hasnormmap = false;
    public boolean hasspecpow = false;
    public boolean hasreflmap = false;
    public boolean hascolmap = false;
    public boolean hastrans = false;

    public Material(String name) {
        this.name = name;
    }

    public Material(String name, String texpath, GGInputStream in) throws IOException {
        this.name = name;
        this.texpath = texpath;

        this.ka = in.readVector3f();
        this.kd = in.readVector3f();
        this.ks = in.readVector3f();
        this.tf = in.readVector3f();

        this.illumModel = in.readInt();
        this.dHalo = in.readBoolean();
        this.dFactor = in.readDouble();
        this.nsExponent = in.readDouble();
        this.sharpnessValue = in.readDouble();
        this.niOpticalDensity = in.readDouble();

        this.mapKaFilename = in.readString();
        this.mapKdFilename = in.readString();
        this.mapKsFilename = in.readString();
        this.mapNsFilename = in.readString();
        this.mapDFilename = in.readString();
        this.decalFilename = in.readString();
        this.dispFilename = in.readString();
        this.bumpFilename = in.readString();

        this.reflType = in.readInt();
        this.reflFilename = in.readString();
    }

    public void loadTextures() {
        if (mapKdFilename != null && !mapKdFilename.isEmpty()) {
            hascolmap = true;
            Kd = Texture.get2DTexture(texpath + mapKdFilename);
        }
        if (mapKaFilename != null && !mapKaFilename.isEmpty()) {
            hasreflmap = true;
            Ka = Texture.get2DTexture(texpath + mapKaFilename);
        }
        if (mapKsFilename != null && !mapKsFilename.isEmpty()) {
            hasspecmap = true;
            Ks = Texture.get2DTexture(texpath + mapKsFilename);
        }
        if (mapNsFilename != null && !mapNsFilename.isEmpty()) {
            hasspecpow = true;
            Ns = Texture.get2DTexture(texpath + mapNsFilename);
        }
        if (mapDFilename != null && !mapDFilename.isEmpty()) {
            hastrans = true;
            D = Texture.get2DTexture(texpath + mapDFilename);
        }
        if (bumpFilename != null && !bumpFilename.isEmpty()) {
            hasnormmap = true;
            norm = Texture.get2DTexture(texpath + bumpFilename);
        }
    }

    public void toFileFormat(GGOutputStream out) throws IOException {
        out.write(name);
        
        out.write(ka);
        out.write(kd);
        out.write(ks);
        out.write(tf);
        
        out.write(illumModel);
        out.write(dHalo);
        out.write(dFactor);
        out.write(nsExponent);
        out.write(sharpnessValue);
        out.write(niOpticalDensity);

        if (mapKaFilename != null) {
            out.write(mapKaFilename);
        } else {
            out.write(0);
        }
        if (mapKdFilename != null) {
            out.write(mapKdFilename);
        } else {
            out.write(0);
        }
        if (mapKsFilename != null) {
            out.write(mapKsFilename);
        } else {
            out.write(0);
        }
        if (mapNsFilename != null) {
            out.write(mapNsFilename);
        } else {
            out.write(0);
        }
        if (mapDFilename != null) {
            out.write(mapDFilename);
        } else {
            out.write(0);
        }
        if (decalFilename != null) {
            out.write(decalFilename);
        } else {
            out.write(0);
        }
        if (dispFilename != null) {
            out.write(dispFilename);
        } else {
            out.write(0);
        }
        if (bumpFilename != null) {
            out.write(bumpFilename);
        } else {
            out.write(0);
        }
        
        out.write(reflType);
        
        if (reflFilename != null) {
            out.write(reflFilename);
        } else {
            out.write(0);
        }

    }
}
