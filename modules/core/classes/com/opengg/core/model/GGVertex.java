package com.opengg.core.model;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;

public class GGVertex {
    public Vector3f position;
    public Vector3f normal;
    public Vector3f tangent = new Vector3f();
    public Vector4f color = new Vector4f(1,1,1,1);
    public Vector2f uvs;
    public Vector4f jointIndices;
    public Vector4f weights;

    public GGVertex(Vector3f position, Vector3f normal, Vector2f uvs){
        this.position = position;
        this.normal = normal;
        this.tangent = tangent;
        this.uvs = uvs;
    }

    public GGVertex(Vector3f position, Vector3f normal, Vector3f tangent, Vector2f uvs){
        this.position = position;
        this.normal = normal;
        this.tangent = tangent;
        this.uvs = uvs;
    }
}
