/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

/**
 *
 * @author ethachu19
 */
public class Matrix3f {
    public float m00, m01, m02;
    public float m10, m11, m12;
    public float m20, m21, m22;
    
    public Matrix3f() {
        m00 = m01 = m02 = 0;
        m10 = m11 = m12 = 0;
        m20 = m21 = m22 = 0;
    }
    
    public Matrix3f(float m11, float m12, float m13,
            float m21, float m22, float m23,
            float m31, float m32, float m33) {
        this.m00 = m11;
        this.m01 = m12;
        this.m02 = m13;
        this.m10 = m21;
        this.m11 = m22;
        this.m12 = m23;
        this.m20 = m31;
        this.m21 = m32;
        this.m22 = m33;
    }
    
    public Matrix3f(Vector3f m1,
            Vector3f m2,
            Vector3f m3) {
        this.m00 = m1.x;
        this.m01 = m1.y;
        this.m02 = m1.z;
        this.m10 = m2.x;
        this.m11 = m2.y;
        this.m12 = m2.z;
        this.m20 = m3.x;
        this.m21 = m3.y;
        this.m22 = m3.z;
    }
    
    public Matrix3f(Matrix4f m) {
        this.m00 = m.m00;
        this.m01 = m.m01;
        this.m02 = m.m02;
        this.m10 = m.m10;
        this.m11 = m.m11;
        this.m12 = m.m12;
        this.m20 = m.m20;
        this.m21 = m.m21;
        this.m22 = m.m22;
    }
    
    public Matrix3f(float[][] arr) {
        this.m00 = arr[0][0];
        this.m01 = arr[0][1];
        this.m02 = arr[0][2];
        this.m10 = arr[1][0];
        this.m11 = arr[1][1];
        this.m12 = arr[1][2];
        this.m20 = arr[2][0];
        this.m21 = arr[2][1];
        this.m22 = arr[2][2];
    }

    public float m00() {
        return m00;
    }

    public float m01() {
        return m01;
    }

    public float m02() {
        return m02;
    }

    public float m10() {
        return m10;
    }

    public float m11() {
        return m11;
    }

    public float m12() {
        return m12;
    }

    public float m20() {
        return m20;
    }

    public float m21() {
        return m21;
    }

    public float m22() {
        return m22;
    }

    public Vector3f multiply(Vector3f v) {
        Vector3fm result = new Vector3fm();
        result.x = m00*v.x + m01* v.y + m02 * v.z;
        result.y = m10*v.x + m11* v.y + m12 * v.z;
        result.z = m20*v.x + m21* v.y + m22 * v.z;
        return new Vector3f(result);
    }
    
    public Matrix3f scale(float scalar) {
        Matrix3f result = new Matrix3f();
        result.m00 = m00*scalar;
        result.m01 = m01*scalar;
        result.m02 = m02*scalar;
        result.m10 = m10*scalar;
        result.m11 = m11*scalar;
        result.m12 = m12*scalar;
        result.m20 = m20*scalar;
        result.m21 = m21*scalar;
        result.m22 = m22*scalar;
        return result;
    }

    public Matrix3f rotation(Quaternionf quat) {
        float w2 = quat.w() * quat.w();
        float x2 = quat.x() * quat.x();
        float y2 = quat.y() * quat.y();
        float z2 = quat.z() * quat.z();
        float zw = quat.z() * quat.w(), dzw = zw + zw;
        float xy = quat.x() * quat.y(), dxy = xy + xy;
        float xz = quat.x() * quat.z(), dxz = xz + xz;
        float yw = quat.y() * quat.w(), dyw = yw + yw;
        float yz = quat.y() * quat.z(), dyz = yz + yz;
        float xw = quat.x() * quat.w(), dxw = xw + xw;
        Matrix3f result = new Matrix3f();
        result.m00 = w2 + x2 - z2 - y2;
        result.m01 = dxy + dzw;
        result.m02 = dxz - dyw;
        result.m10 = -dzw + dxy;
        result.m11 = y2 - z2 + w2 - x2;
        result.m12 = dyz + dxw;
        result.m20 = dyw + dxz;
        result.m21 = dyz - dxw;
        result.m22 = z2 - y2 - x2 + w2;
        return result;
    }

    public Matrix3f multiply(Matrix3f right) {
        float nm00 = m00 * right.m00() + m10 * right.m01() + m20 * right.m02();
        float nm01 = m01 * right.m00() + m11 * right.m01() + m21 * right.m02();
        float nm02 = m02 * right.m00() + m12 * right.m01() + m22 * right.m02();
        float nm10 = m00 * right.m10() + m10 * right.m11() + m20 * right.m12();
        float nm11 = m01 * right.m10() + m11 * right.m11() + m21 * right.m12();
        float nm12 = m02 * right.m10() + m12 * right.m11() + m22 * right.m12();
        float nm20 = m00 * right.m20() + m10 * right.m21() + m20 * right.m22();
        float nm21 = m01 * right.m20() + m11 * right.m21() + m21 * right.m22();
        float nm22 = m02 * right.m20() + m12 * right.m21() + m22 * right.m22();
        Matrix3f dest = new Matrix3f();
        dest.m00 = nm00;
        dest.m01 = nm01;
        dest.m02 = nm02;
        dest.m10 = nm10;
        dest.m11 = nm11;
        dest.m12 = nm12;
        dest.m20 = nm20;
        dest.m21 = nm21;
        dest.m22 = nm22;
        return dest;
    }

    public float determinant() {
        return (m11*m22 - m12*m21) + -1*(m10*m22 - m20*m12) + (m10*m21 - m11*m20);
    }
    
    public Matrix3f inverse() {
        Matrix3f result = new Matrix3f();
        float det = determinant();
        if (det == 0)
            throw new ArithmeticException("Determinant of matrix cannot be zero");
        result.m00 = m11*m22 - m12*m21;
        result.m01 = m21*m02 - m01*m22;
        result.m02 = m01*m12 - m02*m11;
        result.m10 = m12*m20 - m10*m22;
        result.m11 = m00*m22 - m02*m20;
        result.m12 = m02*m10 - m00*m12;
        result.m20 = m10*m21 - m11*m20;
        result.m21 = m01*m20 - m00*m21;
        result.m22 = m00*m11 - m01*m10;
        result = result.scale(1f/det);
        return result;
    }
    
    public float[][] getArr() {
        float[][] arr = {{m00, m01, m02},
        {m10, m11, m12},
        {m20, m21, m22}};

        return arr;
    }
    
    public float access(int x, int y) {
        return getArr()[x][y];
    }
}
