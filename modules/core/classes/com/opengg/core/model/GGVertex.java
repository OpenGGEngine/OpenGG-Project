package com.opengg.core.model;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;

public class
GGVertex {
    public Vector3f position;
    public Vector3f normal;
    public Vector3f tangent;
    public Vector2f uvs;
    public Vector4f jointIndices;
    public Vector4f weights;

    public GGVertex(Vector3f position,Vector3f normal,Vector3f tangent,Vector2f uvs){
        this.position = position;
        this.normal = normal;
        this.tangent = tangent;
        this.uvs = uvs;
    }
}
