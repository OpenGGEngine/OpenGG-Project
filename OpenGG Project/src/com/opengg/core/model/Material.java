/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.model.BuilderInterface;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;
import com.opengg.core.render.texture.Texture;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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

    public boolean hasspecmap = false;
    public boolean hasnormmap = false;
    public boolean hasspecpow = false;
    public boolean hasreflmap = false;
    public boolean hascolmap = false;
    public boolean hastrans = false;

    public Material(String name) {
        this.name = name;
    }

    public Material(String name,String texpath, DataInputStream in) throws IOException {
        this.name = name;

        this.ka.x = in.readFloat();
        this.ka.y = in.readFloat();
        this.ka.z = in.readFloat();

        this.kd.x = in.readFloat();
        this.kd.y = in.readFloat();
        this.kd.z = in.readFloat();

        this.ks.x = in.readFloat();
        this.ks.y = in.readFloat();
        this.ks.z = in.readFloat();

        this.tf.x = in.readFloat();
        this.tf.y = in.readFloat();
        this.tf.z = in.readFloat();

        this.illumModel = in.readInt();

        this.dHalo = in.readBoolean();

        this.dFactor = in.readDouble();

        this.nsExponent = in.readDouble();

        this.sharpnessValue = in.readDouble();

        this.niOpticalDensity = in.readDouble();

        int len = in.readInt();
        System.out.println("Optical");
        System.out.println(len);
        if (len != 0) {
            name = "";
            for (int i = 0; i < len; i++) {
                name += in.readChar();
            }
            this.mapKaFilename = texpath + name;
        } else {
            this.mapKaFilename = null;
        }

        len = in.readInt();
        if (len != 0) {
            name = "";
            for (int i = 0; i < len; i++) {
                name += in.readChar();
            }
            this.mapKdFilename = texpath + name;
            System.out.println(name);
        } else {
            this.mapKdFilename = null;
        }

        len = in.readInt();
        if (len != 0) {
            name = "";
            for (int i = 0; i < len; i++) {
                name += in.readChar();
            }
            this.mapKsFilename = texpath + name;
        } else {
            this.mapKsFilename = null;
        }

        len = in.readInt();
        if (len != 0) {
            name = "";
            for (int i = 0; i < len; i++) {
                name += in.readChar();
            }
            this.mapNsFilename = texpath + name;
        } else {
            this.mapNsFilename = null;
        }

        len = in.readInt();
        if (len != 0) {
            name = "";
            for (int i = 0; i < len; i++) {
                name += in.readChar();
            }
            this.mapDFilename = texpath + name;
        } else {
            this.mapDFilename = null;
        }

        len = in.readInt();
        if (len != 0) {
            name = "";
            for (int i = 0; i < len; i++) {
                name += in.readChar();
            }
            this.decalFilename = texpath + name;
        } else {
            this.decalFilename = null;
        }

        len = in.readInt();
        if (len != 0) {
            name = "";
            for (int i = 0; i < len; i++) {
                name += in.readChar();
            }
            this.dispFilename = texpath + name;
        } else {
            this.dispFilename = null;
        }

        len = in.readInt();
        if (len != 0) {
            name = "";
            for (int i = 0; i < len; i++) {
                name += in.readChar();
            }
            this.bumpFilename = texpath + name;
        } else {
            this.bumpFilename = null;
        }

        this.reflType = in.readInt();

        len = in.readInt();
        if (len != 0) {
            name = "";
            for (int i = 0; i < len; i++) {
                name += in.readChar();
            }
            this.reflFilename = texpath + name;
        } else {
            this.reflFilename = null;
        }
    }

    public void loadTextures() {
        if (mapKdFilename != null) {
            hascolmap = true;
            Kd = Texture.get2DTexture(mapKdFilename);
        }
        if (mapKaFilename != null) {
            hasreflmap = true;
            Ka = Texture.get2DTexture(mapKaFilename);
        }
        if (mapKsFilename != null) {
            hasspecmap = true;
            Ks = Texture.get2DTexture(mapKsFilename);
        }
        if (mapNsFilename != null) {
            hasspecpow = true;
            Ns = Texture.get2DTexture(mapNsFilename);
        }
        if (mapDFilename != null) {
            hastrans = true;
            D = Texture.get2DTexture(mapDFilename);
        }
        if (bumpFilename != null) {
            hasnormmap = true;
            norm = Texture.get2DTexture(bumpFilename);
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
