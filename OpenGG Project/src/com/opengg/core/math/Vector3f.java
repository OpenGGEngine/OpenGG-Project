/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import static com.opengg.core.math.FastMath.isEqual;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class Vector3f implements Serializable{

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

    public Vector3f add(Vector3f v){
        return new Vector3f(this).addThis(v);
    }
    
    public Vector3f addThis(Vector3f v){
        set(x + v.x, y + v.y, z + v.z);
        return this;
    }
    
    public Vector3f add(Vector3f[] v){
        Vector3f sum = new Vector3f(this);
        for(Vector3f n : v)
             sum.addThis(n);
        return sum;
    }
    
    public Vector3f addThis(Vector3f[] v){
        for(Vector3f n : v)
             this.addThis(n);
        return this;
    }
    
    public Vector3f add(float f){
        return new Vector3f(this).addThis(f);
    }
    
    public Vector3f addThis(float f){
        set(x + f, y + f, z + f);
        return this;
    }
    
    public Vector3f subtract(Vector3f v){
        return new Vector3f(this).subtractThis(v);
    }
    
    public Vector3f subtractThis(Vector3f v){
        set(x - v.x, y - v.y, z - v.z);
        return this;
    }
    
    public Vector3f subtract(Vector3f[] v){
        Vector3f diff = new Vector3f(this);
        for(Vector3f n : v)
             diff.subtractThis(n);
        return diff;
    }
    
    public Vector3f subtractThis(Vector3f[] v){
        for(Vector3f n : v)
             this.subtractThis(n);
        return this;
    }
    
    public Vector3f subtract(float f){
        return new Vector3f(this).addThis(f);
    }
    
    public Vector3f subtractThis(float f){
        set(x - f, y - f, z - f);
        return this;
    }
    
    public float getDistance(Vector3f v) {
        return (float) Math.sqrt(Math.pow((this.x - v.x), 2) + Math.pow((this.y - v.y), 2) + Math.pow((this.z - v.z), 2));
    }
    
    public static float getDistance(Vector3f v1, Vector3f v2){
        return (float) Math.sqrt(Math.pow((v2.x - v1.x), 2)+Math.pow((v2.y - v1.y), 2)+Math.pow((v2.z - v1.z), 2));  
    }
    
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
    
    public Vector3f inverse() {
        return new Vector3f(this).invertThis();
    }
    
    public Vector3f invertThis() {
        set(this.x * -1, this.y * -1, this.z * -1);
        return this;
    }
    
    public Vector3f reciprocal(){
        return new Vector3f(this).reciprocateThis();
    }
    
    public Vector3f reciprocateThis(){
         set(1/this.x, 1/this.y, 1/this.z);
         return this;
    }
    
    public Vector3f normalize() {
        return divide(length());
    }
    
    public Vector3f normalizeThis() {
        return divideThis(length());
    }

    public float dot(Vector3f v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    private double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public Vector3f divide(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiply(1f / scalar);
    }
    
    public Vector3f divideThis(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiplyThis(1f / scalar);
    }
    
    public Vector3f divide(Matrix3f m) {
        return m.inverse().multiply(this);
    }
    
    public Vector3f multiply(float scalar) {
        return new Vector3f(this).multiplyThis(scalar);
    }
    
    public Vector3f multiplyThis(float scalar){
        set(x * scalar, y * scalar, z * scalar);
        return this;
    }
    
    public Vector3f multiply(Vector3f v) {
        return new Vector3f(this).multiplyThis(v);
    }
    
    public Vector3f multiplyThis(Vector3f v){
        set(x * v.x, y * v.y, z * v.z);
        return this;
    }
 
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3f transformByQuat(Quaternionf q){
        return new Vector3f(this).transformThisByQuat(q);
    }
    
    public Vector3f transformThisByQuat(Quaternionf q){
        q.transform(this);
        return this;
    }
    
    public void setRadius(float radi) {
        float inclination = getInclination();
        float azimuth = getAzimuth();

        this.x = (float) (radi * Math.sin(Math.toRadians(inclination)) * Math.cos(Math.toRadians(azimuth)));
        this.z = (float) (radi * Math.sin(Math.toRadians(inclination)) * Math.sin(Math.toRadians(azimuth)));
        this.y = (float) (radi * Math.cos(Math.toRadians(inclination)));
    }

    public float getInclination() {
        return (float) Math.toDegrees(Math.acos(y / length()));
    }

    public float getAzimuth() {
        return (float) Math.toDegrees(Math.atan2(z, x));
    }
    
    public void setInclination(float deg){
        float length = length();
        float azimuth = getAzimuth();
        
        x = (float) (length * Math.sin(Math.toRadians(deg)) * Math.cos(Math.toRadians(azimuth)));
        y = (float) (length * Math.sin(Math.toRadians(deg)) * Math.sin(Math.toRadians(azimuth)));
        z = (float) (length * Math.cos(Math.toRadians(deg)));
    }
    
    public void setAzimuth(float deg){
        float length = length();
        float inclination = getInclination();
        
        x = (float) (length * Math.sin(Math.toRadians(inclination)) * Math.cos(Math.toRadians(deg)));
        z = (float) (length * Math.sin(Math.toRadians(inclination)) * Math.sin(Math.toRadians(deg)));
    }
    
    public Vector3f closertoZero(float f){
        float signX = x < 0 ? -1 : 1;
        float signY = y < 0 ? -1 : 1;
        float signZ = z < 0 ? -1 : 1;
        this.x = (Math.abs(x) - f)*signX;
        this.y = (Math.abs(y) - f)*signY;
        this.z = (Math.abs(z) - f)*signZ;
        return this;
    }
    
    public Vector3f closertoZero(Vector3f v){
        float signX = x < 0 ? -1 : 1;
        float signY = y < 0 ? -1 : 1;
        float signZ = z < 0 ? -1 : 1;
        this.x = (Math.abs(x) - v.x)*signX;
        this.y = (Math.abs(y) - v.y)*signY;
        this.z = (Math.abs(z) - v.z)*signZ;
        return this;
    }
    
    public static FloatBuffer listToBuffer(Vector3f... list){
        FloatBuffer f = MemoryUtil.memAllocFloat(3* list.length);
        for(Vector3f v : list){ 
           f.put(v.x);
           f.put(v.y);
           f.put(v.z);
        }
        f.flip();
        return f;
    }
    
    public static Vector3f lerp(Vector3f sv, Vector3f other, float t ){
        return new Vector3f().lerpThis(sv, other, t);
    }
    
    public Vector3f lerpThis(Vector3f sv, Vector3f other, float t ) {
        x = sv.x + (other.x - sv.x) * t;
        y = sv.y + (other.y - sv.y) * t;
        z = sv.z + (other.z - sv.z) * t;
        return this;
    }

    public Vector3f reflect(Vector3f normal) {
        float dot = this.dot(normal);
        x = x - (dot + dot) * normal.x;
        y = y - (dot + dot) * normal.y;
        z = z - (dot + dot) * normal.z;
        return this;
    }
    
    public void zero(){
        this.x = this.y = this.z = 0;
    }
    
    public FloatBuffer getBuffer() {
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.callocFloat(3);
            buffer.put(x).put(y).put(z);
            buffer.flip();
            return buffer;
        }
    }
    
    public byte[] toByteArray(){   
        ByteBuffer b = MemoryUtil.memAlloc(12);
        return b.putFloat(x).putFloat(y).putFloat(z).array();
    }
    
    @Override
    public boolean equals(Object ot){
        if(ot instanceof Vector3f){
            Vector3f o = (Vector3f) ot;
            if(!isEqual(o.x, this.x))
                return false;
            if(!isEqual(o.y, this.y))
                return false;
            if(!isEqual(o.z, this.z))
                return false;
            return true;
        }   
        return false;
    }
    
    @Override
    public String toString(){
        return x + ", " + y + ", " + z;
    }
}
