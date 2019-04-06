/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
import com.opengg.core.math.geom.Ray;

/**
 *
 * @author ethachu19
 */
public class PhysicsRay {
    Vector3f dir;
    Vector3f pos;
    float length;
    
    public PhysicsRay(Vector3f dir, Vector3f pos, float length) {
        this.dir = dir.normalize();
        this.pos = pos;
        this.length = length;
    }
    
    public Vector3f vectorBetween(Vector3f vec) {
        return pos.subtract(vec).subtract(dir.multiply(pos.subtract(vec).dot(dir)));
    }
    
    public boolean isColliding(AABB box) {
        return box.isColliding(this.getRay());
    }

    public Vector3f getDir() {
        return dir;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Ray getRay(){
        return new Ray(pos, dir);
    }
}
