/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import static com.opengg.core.math.FastMath.cosFromSin;
import static com.opengg.core.math.FastMath.sin;
import java.io.Serializable;

/**
 *
 * @author ethachu19
 */
public class Quaternionf implements Serializable{

    /**
     * Angle/Scalar
     */
    public float w;

    /**
     * Angle Vector/Axis of Rotation
     *
     * @see Vector3f
     */
    public float x, y, z;

    public Quaternionf() {
        x = y = z = 0;
        w = 1;
    }

    public Quaternionf(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternionf(Quaternionf q) {
        this.w = q.w;
        x = q.x;
        y = q.y;
        z = q.z;
    }

    public Quaternionf(float angle, Vector3f axis) {
        setAngle(angle);
        x = axis.x;
        y = axis.y;
        z = axis.z;
    }
    
    public Quaternionf(Vector3f euler){
        rotationXYZ(euler.x, euler.y, euler.z);
    }
        
    public Quaternionf(Matrix4f matrix) {
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

    public Quaternionf add(Quaternionf q) {
        return new Quaternionf(this.x + q.x, this.y + q.y, this.z + q.z, this.w + q.w);
    }

    public Quaternionf addEquals(Quaternionf q) {
        this.w += q.w;
        this.x += q.x;
        this.y += q.y;
        this.z += q.z;
        return new Quaternionf(this);
    }

    public Quaternionf subtract(final Quaternionf q) {
        return new Quaternionf(this.x - q.x, this.y - q.y, this.z - q.z, this.w - q.w);
    }

    public Quaternionf multiply(Quaternionf q){
        return new Quaternionf(this).multiplyThis(q);
    }
    
    public Quaternionf multiplyThis(Quaternionf q) {
        set(w * q.x + x * q.w + y * q.z - z * q.y,
                 w * q.y - x * q.z + y * q.w + z * q.x,
                 w * q.z + x * q.y - y * q.x + z * q.w,
                 w * q.w - x * q.x - y * q.y - z * q.z);
        return this;
    }

    public Quaternionf multiply(final float scalar){
        return new Quaternionf(this).multiplyThis(scalar);
    }
    
    public Quaternionf multiplyThis(final float scalar) {
        return multiplyThis(new Quaternionf(scalar,scalar,scalar,scalar));
    }

    public Quaternionf divide(Quaternionf divisor){
        return new Quaternionf(this).divideThis(divisor);
    }
    
    public Quaternionf divideThis(final Quaternionf q) {
        if (q.w == 0 || q.x == 0 || q.y == 0 || q.z == 0) {
            throw new ArithmeticException("Divide by zero in quaternion");
        }
        return set(this.w / q.w, this.x / q.x, this.y / q.y, this.z / q.z);
    }

    public Quaternionf divide(final float divisor){
        return new Quaternionf(this).divideThis(divisor);
    }
    
    public Quaternionf divideThis(final float divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Divide by zero");
        }
        return set(this.w / divisor, this.x / divisor, this.y / divisor, this.z / divisor);
    }

    public float length() {
        return (float) Math.sqrt(w * w + x * x + y * y + z * z);
    }

    public Quaternionf normalize(){
        return new Quaternionf(this).normalizeThis();
    }
    
    public Quaternionf normalizeThis() {
        float magnitude = length();
        w /= magnitude;
        x /= magnitude;
        y /= magnitude;
        z /= magnitude;
        return this;
    }

    public Matrix4f convertMatrix() {
        this.normalize();
        return new Matrix4f(
                1.0f - 2.0f * y * y - 2.0f * z * z,     2.0f * x * y + 2.0f * z * w,            2.0f * x * z - 2.0f * y * w,
                2.0f * x * y - 2.0f * z * w,            1.0f - 2.0f * x * x - 2.0f * z * z,     2.0f * y * z + 2.0f * x * w,
                2.0f * x * z + 2.0f * y * w,            2.0f * y * z - 2.0f * x * w,            1.0f - 2.0f * x * x - 2.0f * y * y);
    }
    

    public Vector3f axis(){
        return new Vector3f(x,y,z);
    }
    
    public float angle() {
        return 2.0f * (float) Math.toDegrees(Math.acos(w));
    }
    
    public float dot(Quaternionf otherQuat) {
        return this.x * otherQuat.x + this.y * otherQuat.y + this.z * otherQuat.z + this.w * otherQuat.w;
    }

    public final void addDegrees(float degrees) {
        float res = angle() + degrees;
        while (res > 360) {
            res -= 360;
        }
        while (res < 0) {
            res += 360;
        }
        w = FastMath.cosDeg((res) / 2);
    }
    
    public final void setAngle(float degrees) {
        while (degrees > 360) {
            degrees -= 360;
        }
        while (degrees < 0) {
            degrees += 360;
        }
        w = FastMath.cosDeg((degrees) / 2);
    }

    public final void setAxis(Vector3f axis){
        this.x = axis.x;
        this.y = axis.y;
        this.z = axis.z;
    }
    
    public static final Quaternionf slerp(final Quaternionf a, final Quaternionf b, float t) {
        if (!(t >= 0 && t <= 1)) {
            throw new ArithmeticException("t not in range");
        }
        float flip = 1;

        float cosine = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;

        if (cosine < 0) {
            cosine = -cosine;
            flip = -1;
        }

        if ((1 - cosine) == 0) {
            return a.multiply(1 - t).add(b.multiply(t * flip));
        }

        float theta = (float) Math.acos(cosine);
        float sine = (float) sin(theta);
        float beta = (float) sin((1 - t) * theta) / sine;
        float alpha = (float) sin(t * theta) / sine * flip;

        return a.multiply(beta).add(b.multiply(alpha));
    }
    
    public Quaternionf invert() {
        return new Quaternionf(this).invertThis();
    }
    
    public Quaternionf invertThis() {
        float invNorm = 1.0f / (x * x + y * y + z * z + w * w);
        x = -x * invNorm;
        y = -y * invNorm;
        z = -z * invNorm;
        w = w * invNorm;
        return this;
    }
    
    public Quaternionf invertIndirect() {
        return new Quaternionf(this).invertThisIndirect();
    }
    
    public Quaternionf invertThisIndirect() {
        Vector3f v = this.toEuler();
        v = v.inverse();
        return rotationXYZ(v.x, v.y, v.z);
    }
    
    public Quaternionf set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Quaternionf set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Quaternionf rotationXYZ(float angleX, float angleY, float angleZ) {   
        angleX = (float)Math.toRadians(angleX);
        angleY = (float)Math.toRadians(angleY);
        angleZ = (float)Math.toRadians(angleZ);
        
        float sx = sin(angleX * 0.5f);
        float cx = (float) cosFromSin(sx, angleX * 0.5f);
        float sy = sin(angleY * 0.5f);
        float cy = (float) cosFromSin(sy, angleY * 0.5f);
        float sz = sin(angleZ * 0.5f);
        float cz = (float) cosFromSin(sz, angleZ * 0.5f);

        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        w = cx*cycz - sx*sysz;
        x = sx*cycz + cx*sysz;
        y = cx*sycz - sx*cysz;
        z = cx*cysz + sx*sycz;

        return this;
    }
    
    public Vector3f toEuler(){
        float nx,ny,nz;
        
        float ysqr = y * y;
        
	float t0 = + 2.0f * (w * x + y * z);
	float t1 = + 1.0f - 2.0f * (x * x + ysqr);
	nx = FastMath.atan2(t0, t1);

	float t2 = + 2.0f * (w * y - z * x);
	t2 = t2 > 1.0f ? 1.0f : t2;
	t2 = t2 < -1.0f ? -1.0f : t2;
	ny = (float)Math.asin(t2);

	float t3 = + 2.0f * (w * z + x * y);
	float t4 = + 1.0f - 2.0f * (ysqr + z * z);  
	nz = FastMath.atan2(t3, t4);
        
        nx = (float)Math.toDegrees(nx);
        ny = (float)Math.toDegrees(ny);
        nz = (float)Math.toDegrees(nz);
        
        Vector3f end = new Vector3f(nx,ny,nz);
        
        return end;
    }
    
    public Vector3f transform(Vector3f v){
        float x = v.x;
        float y = v.y;
        float z = v.z;
        
        float w2 = this.w * this.w;
        float x2 = this.x * this.x;
        float y2 = this.y * this.y;
        float z2 = this.z * this.z;
        float zw = this.z * this.w;
        float xy = this.x * this.y;
        float xz = this.x * this.z;
        float yw = this.y * this.w;
        float yz = this.y * this.z;
        float xw = this.x * this.w;

        float m00 = w2 + x2 - z2 - y2;
        float m01 = xy + zw + zw + xy;
        float m02 = xz - yw + xz - yw;
        float m10 = -zw + xy - zw + xy;
        float m11 = y2 - z2 + w2 - x2;
        float m12 = yz + yz + xw + xw;
        float m20 = yw + xz + xz + yw;
        float m21 = yz + yz - xw - xw;
        float m22 = z2 - y2 - x2 + w2;

        float nx = m00 * x + m10 * y + m20 * z;
        float ny = m01 * x + m11 * y + m21 * z;
        float nz = m02 * x + m12 * y + m22 * z;
        return new Vector3f(nx,ny,nz);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Quaternionf other = (Quaternionf) obj;
        if (Float.floatToIntBits(w) != Float.floatToIntBits(other.w))
            return false;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
            return false;
        if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Float.floatToIntBits(this.w);
        hash = 37 * hash + Float.floatToIntBits(this.x);
        hash = 37 * hash + Float.floatToIntBits(this.y);
        hash = 37 * hash + Float.floatToIntBits(this.z);
        return hash;
    }
    
    @Override
    public String toString(){
        return this.toEuler().toString();
    }

}
