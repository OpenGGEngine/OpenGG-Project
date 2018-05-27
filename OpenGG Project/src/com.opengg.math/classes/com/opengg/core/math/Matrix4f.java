/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import com.opengg.core.system.Allocator;
import java.nio.FloatBuffer;

/**
 *
 * @author Javier
 */
public class Matrix4f {

    public final float m00, m01, m02, m03;
    public final float m10, m11, m12, m13;
    public final float m20, m21, m22, m23;
    public final float m30, m31, m32, m33;

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
        this.m00 = l00;
        this.m10 = l10;
        this.m20 = l20;
        this.m30 = l30;
        this.m01 = l01;
        this.m11 = l11;
        this.m21 = l21;
        this.m31 = l31;
        this.m02 = l02;
        this.m12 = l12;
        this.m22 = l22;
        this.m32 = l32;
        this.m03 = l03;
        this.m13 = l13;
        this.m23 = l23;
        this.m33 = l33;
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

    public Matrix4f(float m00, float m01, float m02,
            float m10, float m11, float m12,
            float m20, float m21, float m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = 0;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = 0;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
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

    public FloatBuffer getStackBuffer() {
        FloatBuffer buffer = Allocator.stackAllocFloat(16);
        buffer.put(m00).put(m01).put(m02).put(m03);
        buffer.put(m10).put(m11).put(m12).put(m13);
        buffer.put(m20).put(m21).put(m22).put(m23);
        buffer.put(m30).put(m31).put(m32).put(m33);
        buffer.flip();
        return buffer;
    }
    
    public FloatBuffer getBuffer() {
        FloatBuffer buffer = Allocator.allocFloat(16);
        buffer.put(m00).put(m01).put(m02).put(m03);
        buffer.put(m10).put(m11).put(m12).put(m13);
        buffer.put(m20).put(m21).put(m22).put(m23);
        buffer.put(m30).put(m31).put(m32).put(m33);
        buffer.flip();
        return buffer;
    }

    public FloatBuffer getTransposedBuffer() {
        FloatBuffer buffer = Allocator.allocFloat(16);
        buffer.put(m00).put(m10).put(m20).put(m30);
        buffer.put(m01).put(m11).put(m21).put(m31);
        buffer.put(m02).put(m12).put(m22).put(m32);
        buffer.put(m03).put(m13).put(m23).put(m33);
        buffer.flip();
        return buffer;
    }

    public Vector4f transform(Vector4f init){
        return init.multiply(this);
    }
    
    public Matrix4f rotate(Quaternionf quat) {
        return this.multiply(quat.convertMatrix());
    }

    public Matrix4f rotate(float x, float y, float z) {
        return this.multiply(new Quaternionf(new Vector3f(x,y,z)).convertMatrix());
    }

    public static Matrix4f translate(float x, float y, float z) {
        float nm30 = x;
        float nm31 = y;
        float nm32 = z;

        return new Matrix4f(1,    0,    0,    0,
                            0,    1,    0,    0,
                            0,    0,    1,    0,
                            nm30, nm31, nm32, 1);

    }
    
    public Matrix4f translate(Vector3f p) {
        float nm30 = p.x;
        float nm31 = p.y;
        float nm32 = p.z;

        return this.multiply(Matrix4f.translate(p.x,p.y,p.z));
    }
    
    public Matrix4f add(Matrix4f other) {
        float nm00 = this.m00 + other.m00;
        float nm10 = this.m10 + other.m10;
        float nm20 = this.m20 + other.m20;
        float nm30 = this.m30 + other.m30;
        float nm01 = this.m01 + other.m01;
        float nm11 = this.m11 + other.m11;
        float nm21 = this.m21 + other.m21;
        float nm31 = this.m31 + other.m31;
        float nm02 = this.m02 + other.m02;
        float nm12 = this.m12 + other.m12;
        float nm22 = this.m22 + other.m22;
        float nm32 = this.m32 + other.m32;
        float nm03 = this.m03 + other.m03;
        float nm13 = this.m13 + other.m13;
        float nm23 = this.m23 + other.m23;
        float nm33 = this.m33 + other.m33;

        return new Matrix4f(nm00, nm01, nm02, nm03,
                            nm10, nm11, nm12, nm13,
                            nm20, nm21, nm22, nm23,
                            nm30, nm31, nm32, nm33);
    }
    
    public Matrix4f multiply(Matrix4f other) {
        float nm00 = this.m00 * other.m00 + this.m10 * other.m01 + this.m20 * other.m02 + this.m30 * other.m03;
        float nm01 = this.m01 * other.m00 + this.m11 * other.m01 + this.m21 * other.m02 + this.m31 * other.m03;
        float nm02 = this.m02 * other.m00 + this.m12 * other.m01 + this.m22 * other.m02 + this.m32 * other.m03;
        float nm03 = this.m03 * other.m00 + this.m13 * other.m01 + this.m23 * other.m02 + this.m33 * other.m03;

        float nm10 = this.m00 * other.m10 + this.m10 * other.m11 + this.m20 * other.m12 + this.m30 * other.m13;
        float nm11 = this.m01 * other.m10 + this.m11 * other.m11 + this.m21 * other.m12 + this.m31 * other.m13;
        float nm12 = this.m02 * other.m10 + this.m12 * other.m11 + this.m22 * other.m12 + this.m32 * other.m13;
        float nm13 = this.m03 * other.m10 + this.m13 * other.m11 + this.m23 * other.m12 + this.m33 * other.m13;

        float nm20 = this.m00 * other.m20 + this.m10 * other.m21 + this.m20 * other.m22 + this.m30 * other.m23;
        float nm21 = this.m01 * other.m20 + this.m11 * other.m21 + this.m21 * other.m22 + this.m31 * other.m23;
        float nm22 = this.m02 * other.m20 + this.m12 * other.m21 + this.m22 * other.m22 + this.m32 * other.m23;
        float nm23 = this.m03 * other.m20 + this.m13 * other.m21 + this.m23 * other.m22 + this.m33 * other.m23;

        float nm30 = this.m00 * other.m30 + this.m10 * other.m31 + this.m20 * other.m32 + this.m30 * other.m33;
        float nm31 = this.m01 * other.m30 + this.m11 * other.m31 + this.m21 * other.m32 + this.m31 * other.m33;
        float nm32 = this.m02 * other.m30 + this.m12 * other.m31 + this.m22 * other.m32 + this.m32 * other.m33;
        float nm33 = this.m03 * other.m30 + this.m13 * other.m31 + this.m23 * other.m32 + this.m33 * other.m33;

        return new Matrix4f(nm00, nm01, nm02, nm03,
                            nm10, nm11, nm12, nm13,
                            nm20, nm21, nm22, nm23,
                            nm30, nm31, nm32, nm33);
    }
    
    public static Matrix4f perspective(float fovy, float aspect, float near, float far) {
        float f = (float) (1f / Math.tan(Math.toRadians(fovy) / 2f));
        float nm00 = f / aspect;
        float nm11 = f;
        float nm22 = (far + near) / (near - far);
        float nm23 = -1f;
        float nm32 = (2f * far * near) / (near - far);
        float nm33 = 0f;

        return new Matrix4f(nm00, 0,    0,    0,
                            0,    nm11, 0,    0,
                            0,    0,    nm22, nm23,
                            0,    0,    nm32, nm33);
    }

    public Matrix4f scale(float x, float y, float z) {
        float nm00 = x;
        float nm11 = y;
        float nm22 = z;

        return this.multiply(new Matrix4f(nm00, 0,    0,    0,
                            0,    nm11, 0,    0,
                            0,    0,    nm22, 0,
                            0,    0,    0,    1));
    }
    
    public Matrix4f scale(Vector3f scale) {
        return scale(scale.x, scale.y, scale.z);
    }

    public static Matrix4f frustum(float left, float right, float bottom, float top, float near, float far) {
        float nm00 = (2f * near) / (right - left);
        float nm11 = (2f * near) / (top - bottom);
        float nm20 = (right + left) / (right - left);
        float nm21 = (top + bottom) / (top - bottom);
        float nm22 = -(far + near) / (far - near);
        float nm23 = -1f;
        float nm32 = -(2f * far * near) / (far - near);
        float nm33 = 0f;

        return new Matrix4f(nm00, 0,    0,    0,
                            0,    nm11, 0,    0,
                            nm20, nm21, nm22, nm23,
                            0,    0,    nm32, nm33);
    }

    public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far) {
        float nm00 = 2f / (right - left);
        float nm11 = 2f / (top - bottom);
        float nm22 = -2f / (far - near);
        float nm30 = -(right + left) / (right - left);
        float nm31 = -(top + bottom) / (top - bottom);
        float nm32 = -(far + near) / (far - near);

        return new Matrix4f(nm00, 0,    0,    0,
                            0,    nm11, 0,    0,
                            0,    0,    nm22, 0,
                            nm30, nm31, nm32, 1);
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

        return new Matrix4f(nm00, nm01, nm02, nm03,
                            nm10, nm11, nm12, nm13,
                            nm20, nm21, nm22, nm23,
                            nm30, nm31, nm32, nm33);
    }

    public Matrix4f InitRotation(Vector3f forward, Vector3f up, Vector3f right)
    {
        Vector3f f = forward;
        Vector3f r = right;
        Vector3f u = up;
        System.out.println(f.toString() +" X " + r.toString()  +" Y  "+ u.toString());
        Matrix4f news = new Matrix4f(r.x,r.y,r.z, 0,
                u.x,u.y ,u.z, 0,
                f.x, f.y,f.z, 0,
                0,0,0,0);
       /* m[0][0] = r.x;	m[0][1] = r.y;	m[0][2] = r.z;	m[0][3] = 0;
        m[1][0] = u.x;	m[1][1] = u.y;	m[1][2] = u.z;	m[1][3] = 0;
        m[2][0] = f.x;	m[2][1] = f.y;	m[2][2] = f.z;	m[2][3] = 0;
        m[3][0] = 0;		m[3][1] = 0;		m[3][2] = 0;		m[3][3] = 1;
*/
        return news;
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
