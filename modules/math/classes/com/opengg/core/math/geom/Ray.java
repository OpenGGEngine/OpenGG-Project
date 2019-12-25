package com.opengg.core.math.geom;

import com.opengg.core.math.Vector3f;

public class Ray {
    Vector3f pos;
    Vector3f dir;

    public Ray(Vector3f pos, Vector3f dir) {
        this.pos = pos;
        this.dir = dir;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Vector3f getDir() {
        return dir;
    }

    public void setDir(Vector3f dir) {
        this.dir = dir;
    }
}
