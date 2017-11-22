/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

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
        n = b.v.subtract(a.v).cross(c.v.subtract(a.v)).add(1/1000000000f).normalize();
    }
}
