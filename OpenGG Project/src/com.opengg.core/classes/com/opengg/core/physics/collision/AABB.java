/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsObject;
import static java.lang.Math.abs;
import java.util.List;

/**
 *
 * @author ethachu19
 */
public class AABB extends PhysicsObject{
    Vector3f lwh = new Vector3f();

    
    Vector3f min = new Vector3f(1,1,1);
    Vector3f max = new Vector3f(-1,-1,-1);
    
    public AABB(List<Vector3f> points){
        for(Vector3f p : points){
            if(abs(p.x()) > lwh.x()) lwh = lwh.setX(abs(p.x()));
            if(abs(p.y()) > lwh.y()) lwh = lwh.setY(abs(p.y()));
            if(abs(p.z()) > lwh.z()) lwh = lwh.setZ(abs(p.z()));
        }
        recalculate();
    }
    
    public AABB(Vector3f... points){
        this(List.of(points));
    }
    
    public AABB(Vector3f lwh) {
        this.lwh = lwh; 
        recalculate();
    }
    
    public AABB(float length, float width, float height){
        this(new Vector3f(length, width, height));
    }
    
    public Vector3f[] getAABBVertices(){
        return new Vector3f[] {min, max};
    }
    
    public String toString() {
        return "[" + min.toString() + " , " + max.toString() + "]";
    }
 
    
    public void recalculate(){
        Vector3f tscale = getScale();
        Vector3f tpos = getPosition();
        min = new Vector3f(-1,-1,-1).multiply(lwh).multiply(tscale).add(tpos);
        max = new Vector3f(1,1,1).multiply(lwh).multiply(tscale).add(tpos);
    }
    
    public boolean isColliding(AABB x) {
        return ! (max.x() < x.min.x() || 
                  max.y() < x.min.y() ||
                  max.z() < x.min.z() ||
                  min.x() > x.max.x() || 
                  min.y() > x.max.y() ||
                  min.z() > x.max.z());
    }
    
    public boolean isColliding(Vector3f pos){
        return ! (max.x() < pos.x() || 
                  max.y() < pos.y() ||
                  max.z() < pos.z() ||
                  min.x() > pos.x() || 
                  min.y() > pos.y() ||
                  min.z() > pos.z());
    }
    
    public Vector3f getLWH(){
        return lwh;
    }
}
