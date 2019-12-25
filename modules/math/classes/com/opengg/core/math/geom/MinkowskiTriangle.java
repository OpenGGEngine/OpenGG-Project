/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math.geom;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class MinkowskiTriangle {
    public MinkowskiSet a;
    public MinkowskiSet b;
    public MinkowskiSet c;
    public Vector3f n;

    public MinkowskiTriangle(MinkowskiSet a, MinkowskiSet b, MinkowskiSet c) {
        this.a = a;
        this.b = b;
        this.c = c;
        n = b.vec.subtract(a.vec).cross(c.vec.subtract(a.vec)).normalize();
    }
}
