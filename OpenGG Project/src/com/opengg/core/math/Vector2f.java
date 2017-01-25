/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import static com.opengg.core.math.FastMath.isEqual;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

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
    
    /**
    * Returns the degree of angle of vector
    */
    
    public double getAngle()
    {
        return Math.toDegrees(Math.atan2(y, x));
    }
    
    /**
    * Returns magnitude of vector
    */
    
    public double getMagnitude()
    {
        return Math.sqrt((x*x) + (y*y));
    }
    /**
     * Returns a FloatBuffer representation of the vector
     * @return FloatBuffer representation of the vector
     */
    public FloatBuffer getBuffer(){
        FloatBuffer b = MemoryUtil.memAllocFloat(2);
        return b.put(x).put(y);
    }
    /**
     * Returns byte array containing the vector
     * @return Byte array containing the vector
     */
    public byte[] getByteArray() {
        ByteBuffer b = MemoryUtil.memAlloc(8);
        return b.putFloat(x).putFloat(y).array();
    }
    
    @Override
    public boolean equals(Object ot){
        if(ot instanceof Vector2f){
            Vector2f o = (Vector2f) ot;
            if(!isEqual(o.x, this.x))
                return false;
            if(!isEqual(o.y, this.y))
                return false;
            return true;
        }   
        return false;
    }
    
    public static Vector2f getFromByteArray(byte[] vector){
        return new Vector2f(1,1);
    }
}
