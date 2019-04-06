/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import java.io.Serializable;

import static com.opengg.core.math.FastMath.*;

/**
 *
 * @author ethachu19
 */
public class Quaternionf implements Serializable{

    /**
     * Angle/Scalar
     */
    public final float w;

    /**
     * Angle Vector/Axis of Rotation
     *
     * @see Vector3f
     */
    public final float x, y, z;

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
        while (angle > 360) {
            angle -= 360;
        }
        while (angle < 0) {
            angle += 360;
        }
        float s = FastMath.sin((angle) / 2);
        w = FastMath.cosDeg((angle) / 2);
        x = axis.x * s;
        y = axis.y * s;
        z = axis.z * s;
    }
    
    public Quaternionf(Vector3f euler){
        float angleX = euler.x, angleY = euler.y, angleZ = euler.z;
        angleX = (float)Math.toRadians(angleX);
        angleY = (float)Math.toRadians(angleY);
        angleZ = (float)Math.toRadians(angleZ);
        
        float sx = sin(angleX * 0.5f);
        float cx = cos(angleX * 0.5f);
        float sy = sin(angleY * 0.5f);
        float cy = cos(angleY * 0.5f);
        float sz = sin(angleZ * 0.5f);
        float cz = cos(angleZ * 0.5f);

        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        w = cx*cycz - sx*sysz;
        x = sx*cycz + cx*sysz;
        y = cx*sycz - sx*cysz;
        z = cx*cysz + sx*sycz;
    }
//        
//
//    public Quaternionf(Matrix4f matrix) {
//        final float trace = matrix.m11 + matrix.m22 + matrix.m33;
//
//        if (trace > 0) {
//            float root = (float) Math.sqrt(trace + 1.0f);
//            w = 0.5f * root;
//            root = 0.5f / root;
//            x = (matrix.m32 - matrix.m23) * root;
//            y = (matrix.m13 - matrix.m31) * root;
//            z = (matrix.m21 - matrix.m12) * root;
//        } else {
//            int[] next = {2, 3, 1};
//
//            int i = 1;
//            if (matrix.m22 > matrix.m11) {
//                i = 2;
//            }
//            if (matrix.m33 > matrix.access(i, i)) {
//                i = 3;
//            }
//            int j = next[i];
//            int k = next[j];
//
//            float root = (float) Math.sqrt(matrix.access(i, i) - matrix.access(j, j) - matrix.access(k, k) + 1.0f);
//            float[] quaternion = {x, y, z};
//            quaternion[i] = 0.5f * root;
//            root = 0.5f / root;
//            w = (matrix.access(k, j) - matrix.access(j, k)) * root;
//            quaternion[j] = (matrix.access(j, i) + matrix.access(i, j)) * root;
//            quaternion[k] = (matrix.access(k, i) + matrix.access(i, k)) * root;
//        }
//    }

    public Quaternionf add(Quaternionf q) {
        return new Quaternionf(this.w + q.w, this.x + q.x, this.y + q.y, this.z + q.z);
    }

    public Quaternionf subtract(final Quaternionf q) {
        return new Quaternionf(this.w - q.w, this.x - q.x, this.y - q.y, this.z - q.z);
    }

    public Quaternionf multiply(Quaternionf q){
        float nx = w * q.x + x * q.w + y * q.z - z * q.y;
        float ny = w * q.y - x * q.z + y * q.w + z * q.x;
        float nz = w * q.z + x * q.y - y * q.x + z * q.w;
        float nw = w * q.w - x * q.x - y * q.y - z * q.z;
        return new Quaternionf(nw,nx,ny,nz);
    }

    public Quaternionf multiply(final float scalar){
        return multiply(new Quaternionf(scalar,scalar,scalar,scalar));
    }

    public Quaternionf divide(Quaternionf q){
        return new Quaternionf(this.w / q.w, this.x / q.x, this.y / q.y, this.z / q.z);
    }

    public Quaternionf divide(final float divisor){
       return new Quaternionf(this.w / divisor, this.x / divisor, this.y / divisor, this.z / divisor);
    }

    public float length() {
        return (float) Math.sqrt(w * w + x * x + y * y + z * z);
    }

    public Quaternionf normalize(){
        return new Quaternionf(this.divide(this.length()));
    }

    public Matrix4f convertMatrix() {
        Quaternionf q = this.normalize();
        return new Matrix4f(
                1.0f - 2.0f * q.y * q.y - 2.0f * q.z * q.z,     2.0f * q.x * q.y - 2.0f * q.z * q.w,            2.0f * q.x * q.z + 2.0f * q.y * q.w, 0,
                2.0f * q.x * q.y + 2.0f * q.z * q.w,            1.0f - 2.0f * q.x * q.x - 2.0f * q.z * q.z,     2.0f * q.y * q.z - 2.0f * q.x * q.w, 0,
                2.0f * q.x * q.z - 2.0f * q.y * q.w,            2.0f * q.y * q.z + 2.0f * q.x * q.w,            1.0f - 2.0f * q.x * q.x - 2.0f * q.y * y, 0,
                0,                                              0,                                              0,                                        1);
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

    public Quaternionf addDegrees(float degrees) {
        float res = angle() + degrees;
        while (res > 360) {
            res -= 360;
        }
        while (res < 0) {
            res += 360;
        }
        return new Quaternionf(FastMath.cosDeg((res) / 2),x,y,z);
    }

   public static Quaternionf LookAt(Vector3f sourcePoint, Vector3f destPoint)
    {
        Vector3f up = new Vector3f(0,1,0);
        Vector3f forward = new Vector3f(0,0,1);
        Vector3f forwardVector = destPoint.subtract(sourcePoint).normalize();

        float dot = forward.dot(forwardVector);//Vector3f.Dot(Vector3f.forward, forwardVector);

        if (Math.abs(dot - (-1.0f)) < 0.000001f)
        {
            return new Quaternionf(3.1415926535897932f, up.x, up.y, up.z);
        }
        if (Math.abs(dot - (1.0f)) < 0.000001f)
        {
            return new Quaternionf();
        }

        float rotAngle = (float)Math.acos(dot);
        Vector3f rotAxis = forward.cross(forwardVector);//Vector3.Cross(Vector3.forward, forwardVector);
        rotAxis = rotAxis.normalize();
        return new Quaternionf(rotAngle, rotAxis);
    }

    public Quaternionf setAngle(float degrees) {
        while (degrees > 360) {
            degrees -= 360;
        }
        while (degrees < 0) {
            degrees += 360;
        }
        return new Quaternionf(FastMath.cosDeg((degrees) / 2),x,y,z);
    }

    public final Quaternionf setAxis(Vector3f axis){
        return new Quaternionf(w,axis.x,axis.y,axis.z);
    }
    public Matrix4f ToRotationMatrix()
    {
        Vector3f forward =  new Vector3f(2.0f * (x * z - w * y), 2.0f * (y * z + w * x), 1.0f - 2.0f * (x * x + y * y));
        Vector3f up = new Vector3f(2.0f * (x * y + w * z), 1.0f - 2.0f * (x * x + z * z), 2.0f * (y * z - w * x));
        Vector3f right = new Vector3f(1.0f - 2.0f * (y * y + z * z), 2.0f * (x * y - w * z), 2.0f * (x * z + w * y));

        return new Matrix4f().InitRotation(forward, up, right);
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
        float sine = sin(theta);
        float beta = sin((1 - t) * theta) / sine;
        float alpha = sin(t * theta) / sine * flip;

        return a.multiply(beta).add(b.multiply(alpha));
    }

    public Quaternionf slerp (Quaternionf end, float alpha) {
        final float d = this.x * end.x + this.y * end.y + this.z * end.z + this.w * end.w;
        float absDot = d < 0.f ? -d : d;

        // Set the first and second scale for the interpolation
        float scale0 = 1f - alpha;
        float scale1 = alpha;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if ((1 - absDot) > 0.1) {// Get the angle between the 2 quaternions,
            // and then store the sin() of that angle
            final float angle = (float)Math.acos(absDot);
            final float invSinTheta = 1f / (float)Math.sin(angle);

            // Calculate the scale for q1 and q2, according to the angle and
            // it's sine value
            scale0 = ((float)Math.sin((1f - alpha) * angle) * invSinTheta);
            scale1 = ((float)Math.sin((alpha * angle)) * invSinTheta);
        }

        if (d < 0.f) scale1 = -scale1;

        // Calculate the x, y, z and w values for the quaternion by using a
        // special form of linear interpolation for quaternions.
        Quaternionf quat = new Quaternionf((scale0 * w) + (scale1 * end.w),
                (scale0 * x) + (scale1 * end.x),
                (scale0 * y) + (scale1 * end.y),
                (scale0 * z) + (scale1 * end.z)
                );
        // Return the interpolated quaternion
        return quat;
    }

    public Quaternionf NLerp(Quaternionf dest, float lerpFactor, boolean shortest)
    {
        Quaternionf correctedDest = dest;

        if(shortest && this.Dot(dest) < 0)
            correctedDest = new Quaternionf( -dest.w,-dest.x, -dest.y, -dest.z);

        return correctedDest.subtract(this).multiply(lerpFactor).add(this).normalize();
    }

    public Quaternionf SLerp(Quaternionf dest, float lerpFactor, boolean shortest)
    {
        final float EPSILON = 1e3f;

        float cos = this.Dot(dest);
        Quaternionf correctedDest = dest;

        if(shortest && cos < 0)
        {
            cos = -cos;
            correctedDest = new Quaternionf( -dest.w,-dest.x, -dest.y, -dest.z);
        }

        if(Math.abs(cos) >= 1 - EPSILON)
            return NLerp(correctedDest, lerpFactor, false);

        float sin = (float)Math.sqrt(1.0f - cos * cos);
        float angle = (float)Math.atan2(sin, cos);
        float invSin =  1.0f/sin;

        float srcFactor = (float)Math.sin((1.0f - lerpFactor) * angle) * invSin;
        float destFactor = (float)Math.sin((lerpFactor) * angle) * invSin;

        return this.multiply(srcFactor).add(correctedDest.multiply(destFactor));
    }
    
    public Quaternionf invert() {
        float invNorm = 1.0f / (x * x + y * y + z * z + w * w);
        return new Quaternionf(w*invNorm,-x*invNorm,-y*invNorm,-z*invNorm);
    }

    public Quaternionf invertIndirect() {
        Vector3f v = this.toEuler();
        v = v.inverse();
        return new Quaternionf(v);
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
    public float Dot(Quaternionf r)
    {
        return x * r.x + y * r.y + z * r.z + w * r.w;
    }
    public Vector3f transform(Vector3f v){
        return this.convertMatrix().transform(new Vector4f(v)).truncate();
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
        return Float.floatToIntBits(z) == Float.floatToIntBits(other.z);
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
    public String toString() {
        return w + ", " + x + ", " + y + ", " + z;
    }
   
    
}
