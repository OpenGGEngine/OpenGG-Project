/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision.colliders;

import com.opengg.core.math.geom.Triangle;
import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class MeshTriangle {
    public Vector3f a = new Vector3f();
    public Vector3f b = new Vector3f();
    public Vector3f c = new Vector3f();
    public Vector3f center = new Vector3f();
    public BoundingBox boundingBox;
    
    public MeshTriangle(Triangle t){
        this(t.a(),t.b(),t.c());
    }
    
    public MeshTriangle(Vector3f a, Vector3f b, Vector3f c){
        this.a = a;
        this.b = b;
        this.c = c;
        this.center = Vector3f.averageOf(a,b,c);
        boundingBox = new BoundingBox(center.subtract(a), center.subtract(b), center.subtract(c));
        boundingBox.setPosition(center);
    }
}
