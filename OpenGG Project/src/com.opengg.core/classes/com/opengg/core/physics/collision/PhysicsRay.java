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
        double maxDistance = Math.sqrt((Math.pow(box.lwh.x, 2) + Math.pow(box.lwh.y, 2) + Math.pow(box.lwh.z, 2))/4);
        Vector3f betw = vectorBetween(box.getPosition());
        Vector3f shortestPoint = betw.add(box.getPosition());
        Vector3f centeredPoint = shortestPoint.subtract(pos);
        if (length != -1 && 
                (pos.add(dir.multiply(length)).subtract(box.getPosition()).length() > maxDistance)
                || betw.length() > maxDistance
                || Math.signum(centeredPoint.x) != Math.signum(dir.x)
                || Math.signum(centeredPoint.y) != Math.signum(dir.y)
                || Math.signum(centeredPoint.z) != Math.signum(dir.z)
              )
            return false;
//        if (box.collide(shortestPoint))
//            return true;
        return false;
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
