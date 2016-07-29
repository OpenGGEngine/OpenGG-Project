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
public class Vector2f {
    public float x;
    public float y;

    /**
     * Creates a default 2d vector with all values set to 0.
     */
    public Vector2f() {
        this.x = 0f;
        this.y = 0f;
    }
    
    /**
     * Creates a vector based off of 2 points.
     * @param x
     * @param y
     */
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    
    /**
     * Creates a new 2d vector based off another.
     * @param v Vector to be copied
     */
    public Vector2f(Vector2f v){
        this.x = v.x;
        this.y = v.y;
    }
    /**
     * Gets the distance between two vectors.
     * @param v
     * @return 
     */
    
    public float getDistance(Vector2f v){
        return (float) Math.sqrt((this.x * v.x)+(this.y * v.y));  
       
    }
    
    @Override
    public String toString(){
        String s = String.valueOf(this.x) + ", " + String.valueOf(this.y);
        return s;
    }
    
    /*
    Returns the degree of angle of vector
    */
    
    public double getAngle()
    {
        return Math.toDegrees(Math.atan2(y, x));
    }
    
    /*
    Returns magnitude of vector
    */
    
    public double getMagnitude()
    {
        return Math.sqrt((x*x) + (y*y));
    }

    public FloatBuffer getBuffer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
