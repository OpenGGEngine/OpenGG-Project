/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core;

import com.opengg.core.util.DashMath;

/**
 *
 * @author ethachu19
 */
public class Quaternion4f {

    /**
     * Angle/Scalar
     */
    public float w;

    /**
     * Angle Vector/Axis of Rotation
     *
     * @see Vector3f
     */
    public Vector3f axis = new Vector3f();

    public Quaternion4f() {
        w = axis.x = axis.y = axis.z = 0;
    }

    public Quaternion4f(float w, float x, float y, float z) {
        this.w = w;
        this.axis.x = x;
        this.axis.y = y;
        this.axis.z = z;
    }

    public Quaternion4f(Quaternion4f q) {
        this.w = q.w;
        this.axis = new Vector3f(q.axis);
    }

    public Quaternion4f(float angle, Vector3f axis) {
        setAngle(angle);
        this.axis = new Vector3f(axis);
    }

    public Quaternion4f(Matrix4f matrix) {
        final float trace = matrix.m11 + matrix.m22 + matrix.m33;

        if (trace > 0) {
            float root = (float) Math.sqrt(trace + 1.0f);
            w = 0.5f * root;
            root = 0.5f / root;
            axis.x = (matrix.m32 - matrix.m23) * root;
            axis.y = (matrix.m13 - matrix.m31) * root;
            axis.z = (matrix.m21 - matrix.m12) * root;
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
            float[] quaternion = {axis.x, axis.y, axis.z};
            quaternion[i] = 0.5f * root;
            root = 0.5f / root;
            w = (matrix.access(k, j) - matrix.access(j, k)) * root;
            quaternion[j] = (matrix.access(j, i) + matrix.access(i, j)) * root;
            quaternion[k] = (matrix.access(k, i) + matrix.access(i, k)) * root;
        }
    }

    public Quaternion4f add(final Quaternion4f q) {
        return new Quaternion4f(this.w + q.w, this.axis.x + q.axis.x, this.axis.y + q.axis.y, this.axis.z + q.axis.z);
    }

    public void addEquals(final Quaternion4f q) {
        this.w += q.w;
        this.axis.x += q.axis.x;
        this.axis.y += q.axis.y;
        this.axis.z += q.axis.z;
    }

    public Quaternion4f subtract(final Quaternion4f q) {
        return new Quaternion4f(this.w - q.w, this.axis.x - q.axis.x, this.axis.y - q.axis.y, this.axis.z - q.axis.z);
    }

    public Quaternion4f multiply(final Quaternion4f q) {
        return new Quaternion4f(this.w * q.w, this.axis.x * q.axis.x, this.axis.y * q.axis.y, this.axis.z * q.axis.z);
    }

    public Quaternion4f multiply(final float scalar) {
        return new Quaternion4f(this.w * scalar, this.axis.x * scalar, this.axis.y * scalar, this.axis.z * scalar);
    }

    public Quaternion4f divide(final Quaternion4f q) {
        if (q.w == 0 || q.axis.x == 0 || q.axis.y == 0 || q.axis.z == 0) {
            throw new ArithmeticException("Divide by zero in quaternion");
        }
        return new Quaternion4f(this.w / q.w, this.axis.x / q.axis.x, this.axis.y / q.axis.y, this.axis.z / q.axis.z);
    }

    public Quaternion4f divide(final float scalar) {
        if (scalar == 0) {
            throw new ArithmeticException("Divide by zero");
        }
        return new Quaternion4f(this.w / scalar, this.axis.x / scalar, this.axis.y / scalar, this.axis.z / scalar);
    }

    public float length() {
        return (float) Math.sqrt(w * w + axis.x * axis.x + axis.y * axis.y + axis.z * axis.z);
    }

    public void normalize() {
        float magnitude = length();
        w /= magnitude;
        axis.x /= magnitude;
        axis.y /= magnitude;
        axis.z /= magnitude;
    }

    public Matrix4f convertMatrix() {
        this.normalize();
        return new Matrix4f(
                1.0f - 2.0f * axis.y * axis.y - 2.0f * axis.z * axis.z, 2.0f * axis.x * axis.y - 2.0f * axis.z * w, 2.0f * axis.x * axis.z + 2.0f * axis.y * w,
                2.0f * axis.x * axis.y + 2.0f * axis.z * w, 1.0f - 2.0f * axis.x * axis.x - 2.0f * axis.z * axis.z, 2.0f * axis.y * axis.z - 2.0f * axis.x * w,
                2.0f * axis.x * axis.z - 2.0f * axis.y * w, 2.0f * axis.y * axis.z + 2.0f * axis.x * w, 1.0f - 2.0f * axis.x * axis.x - 2.0f * axis.y * axis.y);
    }

    public float angle() {
        return 2.0f * (float) Math.toDegrees(Math.acos(w));
    }

    public final void addDegrees(float degrees) {
        float res = angle() + degrees;
        while (res > 360) {
            res -= 360;
        }
        while (res < 0) {
            res += 360;
        }
        w = (float) DashMath.cos2(Math.toRadians((res) / 2));
    }
    
    public final void setAngle(float degrees) {
        while (degrees > 360) {
            degrees -= 360;
        }
        while (degrees < 0) {
            degrees += 360;
        }
        w = (float) DashMath.cos2(Math.toRadians((degrees) / 2));
    }

    public static final Quaternion4f slerp(final Quaternion4f a, final Quaternion4f b, float t) {
        if (!(t >= 0 && t <= 1)) {
            throw new ArithmeticException("t not in range");
        }
        float flip = 1;

        float cosine = a.w * b.w + a.axis.x * b.axis.x + a.axis.y * b.axis.y + a.axis.z * b.axis.z;

        if (cosine < 0) {
            cosine = -cosine;
            flip = -1;
        }

        if ((1 - cosine) == 0) {
            return a.multiply(1 - t).add(b.multiply(t * flip));
        }

        float theta = (float) Math.acos(cosine);
        float sine = (float) Math.sin(theta);
        float beta = (float) Math.sin((1 - t) * theta) / sine;
        float alpha = (float) Math.sin(t * theta) / sine * flip;

        return a.multiply(beta).add(b.multiply(alpha));
    }
}
