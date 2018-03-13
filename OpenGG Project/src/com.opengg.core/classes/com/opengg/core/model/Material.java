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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
    public String mapKaFilename = "";
    public String mapKdFilename = "";
    public String mapKsFilename = "";
    public String mapNsFilename = "";
    public String mapDFilename = "";
    public String decalFilename = "";
    public String dispFilename = "";
    public String bumpFilename = "";
    public int reflType = BuilderInterface.MTL_REFL_TYPE_UNKNOWN;
    public String reflFilename = "";
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

    //flip your bytebuffers
    public Material(ByteBuffer b) {
        //   b.flip();
        name = readString(b);
        this.ka = new Vector3f(b.getFloat(), b.getFloat(), b.getFloat());
        this.kd = new Vector3f(b.getFloat(), b.getFloat(), b.getFloat());
        this.ks = new Vector3f(b.getFloat(), b.getFloat(), b.getFloat());
        this.mapKdFilename = readString(b);

        this.mapNsFilename = readString(b);

        this.bumpFilename = readString(b);

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
            Kd = Texture.get2DSRGBTexture(texpath + mapKdFilename);
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

    public void writeString(String s, ByteBuffer b) {
        ByteBuffer temp = ByteBuffer.wrap(s.getBytes(Charset.forName("UTF-8")));
        b.putInt(temp.capacity());
        b.put(temp);
    }

    public String readString(ByteBuffer b) {
        int namelength = b.getInt();
        if (namelength == 0) {
            return "";
        }
        byte[] name = new byte[namelength];
        b.get(name);
        return new String(name, Charset.forName("UTF-8"));
    }

    public ByteBuffer toBuffer() throws UnsupportedEncodingException {
        
        ByteBuffer b = ByteBuffer.allocate(4 + (name.length() ) + (3 * (4 * 3)) + 4 + (this.mapKdFilename.length() ) + 4 + (this.mapNsFilename.length() ) + 4 + (this.bumpFilename.length() ));
        b.putInt(name.length());
        b.put(ByteBuffer.wrap(name.getBytes(Charset.forName("UTF-8"))));
        b.put(ka.toByteArray());
        b.put(kd.toByteArray());
        b.put(ks.toByteArray());
        b.putInt(mapKdFilename.length());
        b.put(ByteBuffer.wrap(mapKdFilename.getBytes(Charset.forName("UTF-8"))));
        b.putInt(mapNsFilename.length());
        b.put(ByteBuffer.wrap(mapNsFilename.getBytes(Charset.forName("UTF-8"))));
        b.putInt(bumpFilename.length());
        b.put(ByteBuffer.wrap(bumpFilename.getBytes(Charset.forName("UTF-8"))));

        b.flip();
        return b;
    }

    public int getCap() {
        return 4 + (name.length()) + (3 * (4 * 3)) + 4 + (this.mapKdFilename.length()) + 4 + (this.mapKsFilename.length()) + 4 + (bumpFilename.length());
    }

}
