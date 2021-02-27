/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math.geom;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;

/**
 *
 * @author Javier
 */
public record Triangle(Vector3f a, Vector3f b, Vector3f c, Vector3f n) {
    public Triangle(Vector3f a, Vector3f b, Vector3f c) {
        this(a, b, c, b.subtract(a).cross(c.subtract(a)).normalize());
    }

}
