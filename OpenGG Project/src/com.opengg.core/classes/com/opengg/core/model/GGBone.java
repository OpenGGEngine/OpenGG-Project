package com.opengg.core.model;

import com.opengg.core.math.Matrix4f;

public class GGBone {
    public String name;
    public Matrix4f offset;

    public GGBone(String name,Matrix4f offset){
        this.name = name;
        this.offset = offset;
    }

}
