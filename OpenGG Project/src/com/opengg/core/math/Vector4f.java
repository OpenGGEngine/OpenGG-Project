/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.math;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Javier
 */
public class Vector4f {
    public float x;
    public float y;
    public float z;
    public float w;
    
    public Vector4f(){
        this(0,0,0,0);
    }
    
    public Vector4f(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vector4f(Vector3f v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = 1;
    }
    
    public Vector4f set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;

        return this;
    }
    
    public Vector4f normalize() {
        float invLength = 1.0f / length();
        x *= invLength;
        y *= invLength;
        z *= invLength;
        w *= invLength;

        return this;

    }
    
    public Vector4f add(Vector4f v) {
        x += v.x;
        y += v.y;
        z += v.z;
        w += v.w;

        return this;
    }
    
    public Vector4f sub(Vector4f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        w -= v.w;

        return this;
    }
    
    public Vector4f mul(Matrix4f mat) {
        set(mat.m00 * x + mat.m10 * y + mat.m20 * z + mat.m30 * w,
            mat.m01 * x + mat.m11 * y + mat.m21 * z + mat.m31 * w,
            mat.m02 * x + mat.m12 * y + mat.m22 * z + mat.m32 * w,
            mat.m03 * x + mat.m13 * y + mat.m23 * z + mat.m33 * w);

        return this;
    }
    public Vector4f mult(float scalar) {
        return mult(scalar,scalar,scalar,scalar);
    }
    
    public Vector4f mult(Vector4f other) {
        return mult(other.x, other.y, other.z, other.w);
    }
    
    public Vector4f mult(float x, float y, float z, float w) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        this.w *= w;
        
        return this;
    }
    
    public Vector4f div(float scalar) {
        return div(scalar,scalar,scalar,scalar);
    }
    
    public Vector4f div(Vector4f other) {
        return div(other.x, other.y, other.z, other.w);
    }
    
    public Vector4f div(float x, float y, float z, float w) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        this.w /= w;

        return this;
    }
    
    public Vector4f div(Matrix4f m){
        return this.mul(m.invert());
    }
    
    public float distance(Vector4f v) {
        float dx = v.x - x;
        float dy = v.y - y;
        float dz = v.z - z;
        float dw = v.w - w;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
    }
    
    public float dot(Vector4f v) {
        return x * v.x + y * v.y + z * v.z + w * v.w;
    }
    
     public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }
    
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
    
    
    public FloatBuffer getBuffer() {
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.callocFloat(4);
            buffer.put(x).put(y).put(z).put(w);
            buffer.flip();
            return buffer;
        }
    }
    
    public Vector3f truncate(){
        return new Vector3f(x,y,z);
    }
    
    @Override
    public String toString(){
        return x + ", " + y + ", " + z + ", " + w; 
    }
}
