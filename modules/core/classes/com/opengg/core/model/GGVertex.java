package com.opengg.core.model;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;

public class GGVertex {
    public Vector3f position;
    public Vector3f normal;
    public Vector2f uvs;


    public Vector3f tangent = new Vector3f(0, 1, 0);
    public Vector3f biTangent = new Vector3f(0, 1, 0);
    public Vector4f color = new Vector4f(1,1,1,1);
    public Vector4f jointIndices;
    public Vector4f weights;

    public GGVertex(Vector3f position, Vector3f normal, Vector2f uvs){
        this.position = position;
        this.normal = normal;
        this.tangent = tangent;
        this.uvs = uvs;
    }

    public GGVertex setTangent(Vector3f tangent) {
        this.tangent = tangent;
        return this;
    }

    public GGVertex setBiTangent(Vector3f biTangent) {
        this.biTangent = biTangent;
        return this;
    }

    public GGVertex setColor(Vector4f color) {
        this.color = color;
        return this;
    }
}
