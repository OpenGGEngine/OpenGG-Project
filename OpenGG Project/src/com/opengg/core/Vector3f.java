/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core;
import java.nio.FloatBuffer;
/**
 *
 * @author Javier
 */
public class Vector3f {
    public float x;
    public float y;
    public float z;

    /**
     * Creates a default 3d vector with all values set to 0.
     */
    public Vector3f() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }
    
    /**
     * Creates a vector based off of 3 points.
     * @param x
     * @param y
     * @param z
     */
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    
    /**
     * Creates a new vector based off another.
     * @param v Vector to be copied
     */
    public Vector3f(Vector3f v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    /**
     * Gets the distance between two vectors.
     * @param v
     * @return 
     */
    
    public float getDistance(Vector3f v){
        return (float) Math.sqrt((this.x * v.x)+(this.y * v.y)+(this.z * v.z));  
       
    }

}
