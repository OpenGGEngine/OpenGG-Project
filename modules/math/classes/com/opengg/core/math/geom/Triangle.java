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
public class Triangle {

    public Vector3f a;
    public Vector3f b;
    public Vector3f c;
    public Vector3f n;

    public Triangle(Triangle t) {
        this.a = t.a;
        this.b = t.b;
        this.c = t.c;
        this.n = t.n;
    }
    
    public Triangle(Vector3f a, Vector3f b, Vector3f c) {
        this.a = a;
        this.b = b;
        this.c = c;
        n = b.subtract(a).cross(c.subtract(a));
    }
    
    public Triangle transform(Matrix4f transform){
        a = transform.transform(new Vector4f(a)).truncate();
        a = transform.transform(new Vector4f(b)).truncate();
        a = transform.transform(new Vector4f(c)).truncate();
        n = b.subtract(a).cross(c.subtract(a));
        return this;
    }
}
