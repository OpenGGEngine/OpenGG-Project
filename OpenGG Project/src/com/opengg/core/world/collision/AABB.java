/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.collision;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author ethachu19
 */
public class AABB {
    public static final int MAX = 1, MIN = 0;
    float length, width, height;
    private Vector3f[] vertices = {new Vector3f(), new Vector3f()};
    Vector3f pos = new Vector3f();
    
    public AABB(Vector3f pos, float length, float width, float height) {
        this.length = length;
        this.width = width;
        this.height = height;        
        recenter(pos);
        this.pos = pos;
    }
    
    public Vector3f[] getAABBVertices(){
        return new Vector3f[] {vertices[MIN], vertices[MAX]};
    }
    
    public String toString() {
        return "[" + vertices[MIN].toString() + " , " + vertices[MAX].toString() + "]";
    }
    
    public void recenter(Vector3f pos) {
        vertices[MIN].y = (this.pos.y + pos.y) - height / 2;
        vertices[MIN].x = (this.pos.x + pos.x) - width / 2;
        vertices[MIN].z = (this.pos.z + pos.z) - length / 2;

        vertices[MAX].y = (this.pos.y + pos.y) + height / 2;
        vertices[MAX].x = (this.pos.x + pos.x) + width / 2;
        vertices[MAX].z = (this.pos.z + pos.z) + length / 2;
    }
    
    public boolean isColliding(AABB x) {
        return ! (vertices[MAX].x < x.vertices[MIN].x || 
                  vertices[MAX].y < x.vertices[MIN].y ||
                  vertices[MAX].z < x.vertices[MIN].z ||
                  vertices[MIN].x > x.vertices[MAX].x || 
                  vertices[MIN].y > x.vertices[MAX].y ||
                  vertices[MIN].z > x.vertices[MAX].z);
    }
}
