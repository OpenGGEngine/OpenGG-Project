package com.opengg.core.math.geom;

import com.opengg.core.math.Vector3f;

public record Ray(Vector3f pos, Vector3f dir) {
    public Ray setPos(Vector3f pos) {
        return new Ray(pos, dir);
    }

    public Ray setDir(Vector3f dir) {
        return new Ray(pos, dir);
    }
}
