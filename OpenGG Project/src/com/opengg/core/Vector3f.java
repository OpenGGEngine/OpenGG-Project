/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

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
     *
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
     *
     * @param v Vector to be copied
     */
    public Vector3f(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    /**
     * Gets the distance between two vectors.
     *
     * @param v
     * @return
     */

    public float getDistance(Vector3f v) {
        return (float) Math.sqrt(Math.pow((this.x - v.x), 2) + Math.pow((this.y - v.y), 2) + Math.pow((this.z - v.z), 2));
    }
    
    public static float getDistance(Vector3f v1, Vector3f v2){
        return (float) Math.sqrt(Math.pow((v2.x - v1.x), 2)+Math.pow((v2.y - v1.y), 2)+Math.pow((v2.z - v1.z), 2));  
       
    }
    
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
    
    public Vector3f add(Vector3f v){
        return new Vector3f(this.x + v.x,this.y+v.y,this.z+v.z);
        
    }
    public Vector3f addVectors(Vector3f... v){
        Vector3f sum = new Vector3f();
        for(Vector3f n :v){
            sum.add(n);
        }
        return sum;
        
    }
    
    public Vector3f normalize() {
        return divide(length());
    }

    private double lengthSquared() {
        return x * x + y * y + z * z;
    }

    private Vector3f divide(float scalar) {
        return scale(1f / scalar);
    }

    private Vector3f scale(float scalar) {
        return new Vector3f(x * scalar, y * scalar, z * scalar);
    }

    public FloatBuffer getBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        buffer.put(x).put(y).put(z);
        buffer.flip();
        return buffer;
    }

    public void setRadius(float radi) {
        float inclination = getInclination();
        float azimuth = getAzimuth();

        this.x = (float) (radi * Math.cos(inclination) * Math.cos(azimuth));
        this.y = (float) (radi * Math.cos(inclination) * Math.sin(azimuth));
        this.x = (float) (radi * Math.cos(inclination));
    }

    public float getInclination() {
        return (float) Math.toDegrees(Math.acos(y / length()));
    }

    public float getAzimuth() {
        return (float) Math.toDegrees(Math.atan2(z, x));
    }

}
