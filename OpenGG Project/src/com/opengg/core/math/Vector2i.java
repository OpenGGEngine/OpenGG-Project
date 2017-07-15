/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class Vector2i {
    public int x;
    public int y;

    /**
     * Creates a default 2d vector with all values set to 0.
     */
    public Vector2i() {
        this.x = 0;
        this.y = 0;
    }
    
    /**
     * Creates a vector based off of 2 points.
     * @param x
     * @param y
     */
    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    
    /**
     * Creates a new 2d vector based off another.
     * @param v Vector to be copied
     */
    public Vector2i(Vector2i v){
        this.x = v.x;
        this.y = v.y;
    }
    
    public Vector2i add(Vector2i other){
        return new Vector2i(this).addThis(other);
    }
    
    public Vector2i addThis(Vector2i other){
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    
    /**
     * Gets the distance between two vectors.
     * @param v
     * @return 
     */
    public float getDistance(Vector2i v){
        return (float) Math.sqrt(Math.pow((this.x - v.x), 2)+Math.pow((this.y - v.y), 2));  
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
    public IntBuffer getBuffer(){
        IntBuffer b = MemoryUtil.memAllocInt(2);
        return b.put(x).put(y);
    }
    /**
     * Returns byte array containing the vector
     * @return Byte array containing the vector
     */
    public byte[] getByteArray() {
        ByteBuffer b = MemoryUtil.memAlloc(Integer.BYTES * 2);
        return b.putInt(x).putInt(y).array();
    }
    
    @Override
    public boolean equals(Object ot){
        if(ot instanceof Vector2i){
            Vector2i o = (Vector2i) ot;
            if(x != o.x)
                return false;
            if(y != o.y)
                return false;
            return true;
        }   
        return false;
    }
    
    public static Vector2i getFromByteArray(byte[] vector){
        return new Vector2i(1,1);
    }
    
    @Override
    public String toString(){
        String s = String.valueOf(this.x) + ", " + String.valueOf(this.y);
        return s;
    }
}
