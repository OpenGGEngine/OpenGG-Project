/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core;

/**
 *
 * @author ethachu19
 */
public class Quaternion4f {

    public float w, x, y, z;

    public Quaternion4f() {
        w = x = y = z = 0;
    }

    public Quaternion4f(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion4f(float angle, Vector3f axis) {
        float a = angle * 0.5f;
        float s = (float) Math.sin(a);
        float c = (float) Math.cos(a);
        w = c;
        x = axis.x * s;
        y = axis.y * s;
        z = axis.z * s;
    }

    public Quaternion4f(Matrix4f matrix) {
        final float trace = matrix.m11 + matrix.m22 + matrix.m33;

        if (trace > 0) {
            float root = (float) Math.sqrt(trace + 1.0f);
            w = 0.5f * root;
            root = 0.5f / root;
            x = (matrix.m32 - matrix.m23) * root;
            y = (matrix.m13 - matrix.m31) * root;
            z = (matrix.m21 - matrix.m12) * root;
        } else {
            int[] next = {2, 3, 1};

            int i = 1;
            if (matrix.m22 > matrix.m11) {
                i = 2;
            }
            if (matrix.m33 > matrix.access(i, i)) {
                i = 3;
            }
            int j = next[i];
            int k = next[j];

            float root = (float) Math.sqrt(matrix.access(i, i) - matrix.access(j, j) - matrix.access(k, k) + 1.0f);
            float[] quaternion = {x, y, z};
            quaternion[i] = 0.5f * root;
            root = 0.5f / root;
            w = (matrix.access(k, j) - matrix.access(j, k)) * root;
            quaternion[j] = (matrix.access(j, i) + matrix.access(i, j)) * root;
            quaternion[k] = (matrix.access(k, i) + matrix.access(i, k)) * root;
        }
    }

    public Quaternion4f add(final Quaternion4f q) {
        return new Quaternion4f(this.w + q.w, this.x + q.x, this.y + q.y, this.z + q.z);
    }

    public Quaternion4f subtract(final Quaternion4f q) {
        return new Quaternion4f(this.w - q.w, this.x - q.x, this.y - q.y, this.z - q.z);
    }

    public Quaternion4f multiply(final Quaternion4f q) {
        return new Quaternion4f(this.w * q.w, this.x * q.x, this.y * q.y, this.z * q.z);
    }

    public Quaternion4f multiply(final float scalar) {
        return new Quaternion4f(this.w * scalar, this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Quaternion4f divide(final Quaternion4f q) {
        if (q.w == 0 || q.x == 0 || q.y == 0 || q.z == 0) {
            throw new ArithmeticException("Divide by zero in quaternion");
        }
        return new Quaternion4f(this.w / q.w, this.x / q.x, this.y / q.y, this.z / q.z);
    }

    public Quaternion4f divide(final float scalar) {
        if (scalar == 0) {
            throw new ArithmeticException("Divide by zero");
        }
        return new Quaternion4f(this.w / scalar, this.x / scalar, this.y / scalar, this.z / scalar);
    }

    public float length() {
        return (float) Math.sqrt(w * w + x * x + y * y + z * z);
    }

    public Matrix4f convertMatrix() {
        float fTx = 2.0f * x;
        float fTy = 2.0f * y;
        float fTz = 2.0f * z;
        float fTwx = fTx * w;
        float fTwy = fTy * w;
        float fTwz = fTz * w;
        float fTxx = fTx * x;
        float fTxy = fTy * x;
        float fTxz = fTz * x;
        float fTyy = fTy * y;
        float fTyz = fTz * y;
        float fTzz = fTz * z;

        return new Matrix4f(1.0f - (fTyy + fTzz), fTxy - fTwz, fTxz + fTwy,
                fTxy + fTwz, 1.0f - (fTxx + fTzz), fTyz - fTwx,
                fTxz - fTwy, fTyz + fTwx, 1.0f - (fTxx + fTyy));
    }

    public Vector3f angleVector() {
        float squareLength = x * x + y * y + z * z;
        float inverseLength = 1.0f / (float) Math.pow(squareLength, 0.5f);
        return new Vector3f(x*inverseLength, y*inverseLength, z*inverseLength);
    }
    
    public float angle() {
        return 2.0f * (float) Math.acos(w);
    }
    
    public void rotate(float degrees){
        if (degrees < 0)
            degrees += 360;
        float difference = angle()-degrees;
        w = (float) Math.cos((degrees - difference)/2);
    }
}
