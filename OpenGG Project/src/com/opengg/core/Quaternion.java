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
public class Quaternion {

    public float w, x, y, z;

    public Quaternion() {
        w = x = y = z = 0;
    }

    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion(Matrix4f matrix) {
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

    public Quaternion add(final Quaternion q) {
        return new Quaternion(this.w + q.w, this.x + q.x, this.y + q.y, this.z + q.z);
    }

    public Quaternion subtract(final Quaternion q) {
        return new Quaternion(this.w - q.w, this.x - q.x, this.y - q.y, this.z - q.z);
    }

    public Quaternion multiply(final Quaternion q) {
        return new Quaternion(this.w * q.w, this.x * q.x, this.y * q.y, this.z * q.z);
    }

    public Quaternion multiply(final float scalar) {
        return new Quaternion(this.w * scalar, this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Quaternion divide(final Quaternion q) {
        if (q.w == 0 || q.x == 0 || q.y == 0 || q.z == 0)
            throw new ArithmeticException("Divide by zero in quaternion");
        return new Quaternion(this.w / q.w, this.x / q.x, this.y / q.y, this.z / q.z);
    }

    public Quaternion divide(final float scalar) {
        if (scalar == 0)
            throw new ArithmeticException("Divide by zero");
        return new Quaternion(this.w / scalar, this.x / scalar, this.y / scalar, this.z / scalar);
    }

    public float length() {
        return (float) Math.sqrt(w * w + x * x + y * y + z * z);
    }
    
    
}
