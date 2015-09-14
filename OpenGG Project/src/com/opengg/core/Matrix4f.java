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
public class Matrix4f {
    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;
    
    /**
     * Default Matrix, generates only 0.
     */
    
    public Matrix4f(){
        m00 = 1;
        m10 = 0;
        m20 = 0;
        m30 = 0;
        m01 = 0;
        m11 = 1;
        m21 = 0;
        m31 = 0;
        m02 = 0;
        m12 = 0;
        m22 = 1;
        m32 = 0;
        m03 = 0;
        m13 = 0;
        m23 = 0;
        m33 = 1;
    }
    /**
     * Creates a matrix with predefined values.
     */
    public Matrix4f(float l00, float l01, float l02,float l03,
                    float l10, float l11, float l12,float l13,
                    float l20, float l21, float l22,float l23,
                    float l30, float l31, float l32,float l33){
        m00 = l00;
        m10 = l01;
        m20 = l02;
        m30 = l03;
        m01 = l10;
        m11 = l11;
        m21 = l12;
        m31 = l13;
        m02 = l20;
        m12 = l21;
        m22 = l22;
        m32 = l23;
        m03 = l30;
        m13 = l31;
        m23 = l32;
        m33 = l33;
    }
    
    public FloatBuffer getBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(m00).put(m10).put(m20).put(m30);
        buffer.put(m01).put(m11).put(m21).put(m31);
        buffer.put(m02).put(m12).put(m22).put(m32);
        buffer.put(m03).put(m13).put(m23).put(m33);
        buffer.flip();
        return buffer;
    }
    
    public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far) {
        Matrix4f ortho = new Matrix4f();

        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        ortho.m00 = 2f / (right - left);
        ortho.m11 = 2f / (top - bottom);
        ortho.m22 = -2f / (far - near);
        ortho.m03 = tx;
        ortho.m13 = ty;
        ortho.m23 = tz;

        return ortho;
    }

    public static Matrix4f rotate(float angle, float x, float y, float z) { 
         Matrix4f rotation = new Matrix4f(); 
         float c = (float) Math.cos(Math.toRadians(angle)); 
         float s = (float) Math.sin(Math.toRadians(angle)); 
         Vector3f vec = new Vector3f(x, y, z); 
         if (vec.length() != 1f) { 
             vec = vec.normalize(); 
             x = vec.x; 
             y = vec.y; 
             z = vec.z; 
         } 
        rotation.m00 = x * x * (1f - c) + c;
        rotation.m10 = y * x * (1f - c) + z * s;
        rotation.m20 = x * z * (1f - c) - y * s;
        rotation.m01 = x * y * (1f - c) - z * s;
        rotation.m11 = y * y * (1f - c) + c;
        rotation.m21 = y * z * (1f - c) + x * s;
        rotation.m02 = x * z * (1f - c) + y * s;
        rotation.m12 = y * z * (1f - c) - x * s;
        rotation.m22 = z * z * (1f - c) + c;
         return rotation;
    }
    
    public static Matrix4f translate(float x, float y, float z) { 
         Matrix4f translation = new Matrix4f(); 

 
        translation.m03 = x; 
        translation.m13 = y; 
        translation.m23 = z; 
 
 
        return translation; 
     } 
    
    public Matrix4f add(Matrix4f other) { 
        Matrix4f result = new Matrix4f(); 

        result.m00 = this.m00 + other.m00; 
        result.m10 = this.m10 + other.m10; 
        result.m20 = this.m20 + other.m20; 
        result.m30 = this.m30 + other.m30; 

 
        result.m01 = this.m01 + other.m01; 
        result.m11 = this.m11 + other.m11; 
        result.m21 = this.m21 + other.m21; 
        result.m31 = this.m31 + other.m31; 

 
        result.m02 = this.m02 + other.m02; 
        result.m12 = this.m12 + other.m12; 
        result.m22 = this.m22 + other.m22; 
        result.m32 = this.m32 + other.m32; 
 
 
        result.m03 = this.m03 + other.m03; 
        result.m13 = this.m13 + other.m13; 
        result.m23 = this.m23 + other.m23; 
        result.m33 = this.m33 + other.m33; 

 
        return result; 
    } 
    
    public Matrix4f multiply(Matrix4f other) {
        Matrix4f result = new Matrix4f();

        result.m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20 + this.m03 * other.m30;
        result.m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20 + this.m13 * other.m30;
        result.m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20 + this.m23 * other.m30;
        result.m30 = this.m30 * other.m00 + this.m31 * other.m10 + this.m32 * other.m20 + this.m33 * other.m30;

        result.m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21 + this.m03 * other.m31;
        result.m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21 + this.m13 * other.m31;
        result.m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21 + this.m23 * other.m31;
        result.m31 = this.m30 * other.m01 + this.m31 * other.m11 + this.m32 * other.m21 + this.m33 * other.m31;

        result.m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22 + this.m03 * other.m32;
        result.m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22 + this.m13 * other.m32;
        result.m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22 + this.m23 * other.m32;
        result.m32 = this.m30 * other.m02 + this.m31 * other.m12 + this.m32 * other.m22 + this.m33 * other.m32;

        result.m03 = this.m00 * other.m03 + this.m01 * other.m13 + this.m02 * other.m23 + this.m03 * other.m33;
        result.m13 = this.m10 * other.m03 + this.m11 * other.m13 + this.m12 * other.m23 + this.m13 * other.m33;
        result.m23 = this.m20 * other.m03 + this.m21 * other.m13 + this.m22 * other.m23 + this.m23 * other.m33;
        result.m33 = this.m30 * other.m03 + this.m31 * other.m13 + this.m32 * other.m23 + this.m33 * other.m33;

        return result;
    }

//    public Matrix4f(Matrix4f old){
//        
//    }
}
