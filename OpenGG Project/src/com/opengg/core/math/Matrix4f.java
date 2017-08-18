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
public class Matrix4f {

    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;

    /**
     * Default Matrix, generates only 0.
     */
    public Matrix4f() {
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
    public Matrix4f(float l00, float l01, float l02, float l03,
            float l10, float l11, float l12, float l13,
            float l20, float l21, float l22, float l23,
            float l30, float l31, float l32, float l33) {
        m00 = l00;
        m10 = l10;
        m20 = l20;
        m30 = l30;
        m01 = l01;
        m11 = l11;
        m21 = l21;
        m31 = l31;
        m02 = l02;
        m12 = l12;
        m22 = l22;
        m32 = l32;
        m03 = l03;
        m13 = l13;
        m23 = l23;
        m33 = l33;
    }
    
    public Matrix4f(Matrix4f matrix){
        m00 = matrix.m00;
        m10 = matrix.m10;
        m20 = matrix.m20;
        m30 = matrix.m30;
        m01 = matrix.m01;
        m11 = matrix.m11;
        m21 = matrix.m21;
        m31 = matrix.m31;
        m02 = matrix.m02;
        m12 = matrix.m12;
        m22 = matrix.m22;
        m32 = matrix.m32;
        m03 = matrix.m03;
        m13 = matrix.m13;
        m23 = matrix.m23;
        m33 = matrix.m33;
    }

    public Matrix4f(float m11, float m12, float m13,
            float m21, float m22, float m23,
            float m31, float m32, float m33) {
        this.m00 = m11;
        this.m01 = m12;
        this.m02 = m13;
        this.m03 = 0;
        this.m10 = m21;
        this.m11 = m22;
        this.m12 = m23;
        this.m13 = 0;
        this.m20 = m31;
        this.m21 = m32;
        this.m22 = m33;
        this.m23 = 0;
        this.m30 = 0;
        this.m31 = 0;
        this.m32 = 0;
        this.m33 = 1;
    }

    public Matrix4f(float[][] arr) {
        this.m00 = arr[0][0];
        this.m01 = arr[0][1];
        this.m02 = arr[0][2];
        this.m03 = arr[0][3];
        this.m10 = arr[1][0];
        this.m11 = arr[1][1];
        this.m12 = arr[1][2];
        this.m13 = arr[1][3];
        this.m20 = arr[2][0];
        this.m21 = arr[2][1];
        this.m22 = arr[2][2];
        this.m23 = arr[2][3];
        this.m30 = arr[3][0];
        this.m31 = arr[3][1];
        this.m32 = arr[3][2];
        this.m33 = arr[3][3];
    }

    public FloatBuffer getBuffer() {
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.callocFloat(16);
            buffer.put(m00).put(m01).put(m02).put(m03);
            buffer.put(m10).put(m11).put(m12).put(m13);
            buffer.put(m20).put(m21).put(m22).put(m23);
            buffer.put(m30).put(m31).put(m32).put(m33);
            buffer.flip();
            return buffer;
        }
    }

    public Vector4f transform(Vector4f init){
        return init.mul(this);
    }
    
    public Matrix4f rotateQuat(Quaternionf q) {
        return this.multiply(q.convertMatrix());
    }

    public Matrix4f rotate(float x, float y, float z) {
        return this.multiply(new Quaternionf(new Vector3f(x,y,z)).convertMatrix());
    }

    public static Matrix4f translate(float x, float y, float z) {
        Matrix4f translation = new Matrix4f();

        translation.m30 = x;
        translation.m31 = y;
        translation.m32 = z;

        return translation;
    }
    
    public Matrix4f translate(Vector3f p) {
        Matrix4f translation = new Matrix4f();

        translation.m30 = p.x;
        translation.m31 = p.y;
        translation.m32 = p.z;

        return this.multiply(translation);
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

        result.m00 = this.m00 * other.m00 + this.m10 * other.m01 + this.m20 * other.m02 + this.m30 * other.m03;
        result.m01 = this.m01 * other.m00 + this.m11 * other.m01 + this.m21 * other.m02 + this.m31 * other.m03;
        result.m02 = this.m02 * other.m00 + this.m12 * other.m01 + this.m22 * other.m02 + this.m32 * other.m03;
        result.m03 = this.m03 * other.m00 + this.m13 * other.m01 + this.m23 * other.m02 + this.m33 * other.m03;

        result.m10 = this.m00 * other.m10 + this.m10 * other.m11 + this.m20 * other.m12 + this.m30 * other.m13;
        result.m11 = this.m01 * other.m10 + this.m11 * other.m11 + this.m21 * other.m12 + this.m31 * other.m13;
        result.m12 = this.m02 * other.m10 + this.m12 * other.m11 + this.m22 * other.m12 + this.m32 * other.m13;
        result.m13 = this.m03 * other.m10 + this.m13 * other.m11 + this.m23 * other.m12 + this.m33 * other.m13;

        result.m20 = this.m00 * other.m20 + this.m10 * other.m21 + this.m20 * other.m22 + this.m30 * other.m23;
        result.m21 = this.m01 * other.m20 + this.m11 * other.m21 + this.m21 * other.m22 + this.m31 * other.m23;
        result.m22 = this.m02 * other.m20 + this.m12 * other.m21 + this.m22 * other.m22 + this.m32 * other.m23;
        result.m23 = this.m03 * other.m20 + this.m13 * other.m21 + this.m23 * other.m22 + this.m33 * other.m23;

        result.m30 = this.m00 * other.m30 + this.m10 * other.m31 + this.m20 * other.m32 + this.m30 * other.m33;
        result.m31 = this.m01 * other.m30 + this.m11 * other.m31 + this.m21 * other.m32 + this.m31 * other.m33;
        result.m32 = this.m02 * other.m30 + this.m12 * other.m31 + this.m22 * other.m32 + this.m32 * other.m33;
        result.m33 = this.m03 * other.m30 + this.m13 * other.m31 + this.m23 * other.m32 + this.m33 * other.m33;

        return result;
    }
    
    public static Matrix4f perspective(float fovy, float aspect, float near, float far) {
        Matrix4f perspective = new Matrix4f();

        float f = (float) (1f / Math.tan(Math.toRadians(fovy) / 2f));
        perspective.m00 = f / aspect;
        perspective.m11 = f;
        perspective.m22 = (far + near) / (near - far);
        perspective.m23 = -1f;
        perspective.m32 = (2f * far * near) / (near - far);
        perspective.m33 = 0f;

        return perspective;
    }

    public Matrix4f scale(float x, float y, float z) {
        Matrix4f scaling = new Matrix4f();

        scaling.m00 = x;
        scaling.m11 = y;
        scaling.m22 = z;

        return this.multiply(scaling);
    }
    
    public Matrix4f scale(Vector3f scale) {
        return scale(scale.x, scale.y, scale.z);
    }

    public static Matrix4f frustum(float left, float right, float bottom, float top, float near, float far) {
        Matrix4f frustum = new Matrix4f();

        frustum.m00 = (2f * near) / (right - left);
        frustum.m11 = (2f * near) / (top - bottom);
        frustum.m20 = (right + left) / (right - left);
        frustum.m21 = (top + bottom) / (top - bottom);
        frustum.m22 = -(far + near) / (far - near);
        frustum.m23 = -1f;
        frustum.m32 = -(2f * far * near) / (far - near);
        frustum.m33 = 0f;

        return frustum;
    }

    public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far) {
        Matrix4f ortho = new Matrix4f();
        ortho.m00 = 2f / (right - left);
        ortho.m11 = 2f / (top - bottom);
        ortho.m22 = -2f / (far - near);
        ortho.m30 = -(right + left) / (right - left);
        ortho.m31 = -(top + bottom) / (top - bottom);
        ortho.m32 = -(far + near) / (far - near);

        return ortho;
    }
    
    public Matrix4f invert() {
        float a = m00 * m11 - m01 * m10;
        float b = m00 * m12 - m02 * m10;
        float c = m00 * m13 - m03 * m10;
        float d = m01 * m12 - m02 * m11;
        float e = m01 * m13 - m03 * m11;
        float f = m02 * m13 - m03 * m12;
        float g = m20 * m31 - m21 * m30;
        float h = m20 * m32 - m22 * m30;
        float i = m20 * m33 - m23 * m30;
        float j = m21 * m32 - m22 * m31;
        float k = m21 * m33 - m23 * m31;
        float l = m22 * m33 - m23 * m32;
        float det = a * l - b * k + c * j + d * i - e * h + f * g;
        det = 1.0f / det;
        float nm00 = ( m11 * l - m12 * k + m13 * j) * det;
        float nm01 = (-m01 * l + m02 * k - m03 * j) * det;
        float nm02 = ( m31 * f - m32 * e + m33 * d) * det;
        float nm03 = (-m21 * f + m22 * e - m23 * d) * det;
        float nm10 = (-m10 * l + m12 * i - m13 * h) * det;
        float nm11 = ( m00 * l - m02 * i + m03 * h) * det;
        float nm12 = (-m30 * f + m32 * c - m33 * b) * det;
        float nm13 = ( m20 * f - m22 * c + m23 * b) * det;
        float nm20 = ( m10 * k - m11 * i + m13 * g) * det;
        float nm21 = (-m00 * k + m01 * i - m03 * g) * det;
        float nm22 = ( m30 * e - m31 * c + m33 * a) * det;
        float nm23 = (-m20 * e + m21 * c - m23 * a) * det;
        float nm30 = (-m10 * j + m11 * h - m12 * g) * det;
        float nm31 = ( m00 * j - m01 * h + m02 * g) * det;
        float nm32 = (-m30 * d + m31 * b - m32 * a) * det;
        float nm33 = ( m20 * d - m21 * b + m22 * a) * det;
        m00 = nm00;
        m01 = nm01;
        m02 = nm02;
        m03 = nm03;
        m10 = nm10;
        m11 = nm11;
        m12 = nm12;
        m13 = nm13;
        m20 = nm20;
        m21 = nm21;
        m22 = nm22;
        m23 = nm23;
        m30 = nm30;
        m31 = nm31;
        m32 = nm32;
        m33 = nm33;
        return this;
    }

    public float[][] getArray() {
        float[][] arr = {{m00, m01, m02, m03},
        {m10, m11, m12, m13},
        {m20, m21, m22, m23},
        {m30, m31, m32, m33}};

        return arr;
    }
    
    public float access(int x, int y) {
        return getArray()[x][y];
    }
    
    @Override
    public String toString(){
        return  m00 + ", " + m10 + ", " + m20 + ", " + m30 + ", " + "\n" +
                m01 + ", " + m11 + ", " + m21 + ", " + m31 + ", " + "\n" +
                m02 + ", " + m12 + ", " + m22 + ", " + m32 + ", " + "\n" + 
                m03 + ", " + m13 + ", " + m23 + ", " + m33;
    }
}
