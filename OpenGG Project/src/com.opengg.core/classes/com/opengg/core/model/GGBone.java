package com.opengg.core.model;

import com.opengg.core.math.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class GGBone {
    public String name;
    public Matrix4f offset;

    public GGBone(String name,Matrix4f offset){
        this.name = name;
        this.offset = offset;
    }
    public GGBone(ByteBuffer buff){
        this.name = MLoaderUtils.readString(buff);
        offset = MLoaderUtils.loadMat4(buff);
    }

}
