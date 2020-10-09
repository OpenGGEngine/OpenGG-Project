/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import java.io.Serializable;

import static com.opengg.core.math.FastMath.sin;

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
        axis = axis.normalize();
        while (angle > 360) {
            angle -= 360;
        }
        while (angle < 0) {
            angle += 360;
        }
        float s = sin((angle) / 2);
        w = FastMath.cos((angle) / 2);
        x = axis.x * s;
        y = axis.y * s;
        z = axis.z * s;
    }

    public static Quaternionf createXYZ(Vector3f euler) {
        double pitch = euler.x, yaw = euler.y, roll = euler.z;
        pitch = Math.toRadians(pitch);
        yaw = Math.toRadians(yaw);
        roll = Math.toRadians(roll);

        double sp = Math.sin(pitch * 0.5f);
        double cp = Math.cos(pitch * 0.5f);
        double sy = Math.sin(yaw * 0.5f);
        double cy = Math.cos(yaw * 0.5f);
        double sr = Math.sin(roll * 0.5f);
        double cr = Math.cos(roll * 0.5f);

        var w = (float) (cp*cy*cr - sp*sy*sr);
        var x = (float) (sp*cy*cr + cp*sy*sr);
        var y = (float) (cp*sy*cr - sp*cy*sr);
        var z = (float) (cp*cy*sr + sp*sy*cr);
        return new Quaternionf(w,x,y,z);
    }

    public static Quaternionf createYXZ(Vector3f euler) {
        float x = euler.x, y = euler.y, z = euler.z;
        x = (float) Math.toRadians(x);
        y = (float) Math.toRadians(y);
        z = (float) Math.toRadians(z);

        double sx = Math.sin(x * 0.5f);
        double cx = Math.cos(x * 0.5f);
        double sy = Math.sin(y * 0.5f);
        double cy = Math.cos(y * 0.5f);
        double sz = Math.sin(z * 0.5f);
        double cz = Math.cos(z * 0.5f);

            x = (float) (cy*sx*cz + sy*cx*sz);
            y = (float) (sy*cx*cz - cy*sx*sz);
            z = (float) (cy*cx*sz - sy*sx*cz);
        var w = (float) (cy*cx*cz + sy*sx*sz);
        return new Quaternionf(w,x,y,z);
    }

    public float w(){
        return w;
    }

    public float x(){
        return x;
    }

    public float y(){
        return y;
    }
    public float z(){
        return z;
    }


    public Quaternionf add(Quaternionf q) {
        return new Quaternionf(this.w + q.w, this.x + q.x, this.y + q.y, this.z + q.z);
    }

    public Quaternionf subtract(final Quaternionf q) {
        return new Quaternionf(this.w - q.w, this.x - q.x, this.y - q.y, this.z - q.z);
    }

    public Quaternionf multiply(Quaternionf q){
        float nw = w * q.w - x * q.x - y * q.y - z * q.z;
        float nx = w * q.x + x * q.w - y * q.z + z * q.y;
        float ny = w * q.y + x * q.z + y * q.w - z * q.x;
        float nz = w * q.z - x * q.y + y * q.x + z * q.w;
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

    public Vector3f axis(){
        return new Vector3f(x,y,z);
    }
    
    public float angle() {
        return 2.0f * (float) Math.toDegrees(Math.acos(w));
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

   public static Quaternionf lookAt(Vector3f sourcePoint, Vector3f destPoint)
    {
        Vector3f up = new Vector3f(0,1,0);
        Vector3f forward = new Vector3f(0,0,-1);
        Vector3f forwardVector = destPoint.subtract(sourcePoint).normalize();

        float dot = forward.dot(forwardVector);//Vector3f.Dot(Vector3f.forward, forwardVector);

        if (Math.abs(dot - (-1.0f)) < 0.000001f)
        {
            return new Quaternionf(FastMath.PI, up.x, up.y, up.z);
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

    public static Quaternionf slerp(final Quaternionf a, final Quaternionf b, float t) {
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

    public Quaternionf invert() {
        float invNorm = 1.0f / (x * x + y * y + z * z + w * w);
        return new Quaternionf(w*invNorm,-x*invNorm,-y*invNorm,-z*invNorm);
    }
    
    public Vector3f toEuler(){

	    float t0 = 2.0f * (w * x - y * z);
	    float t1 = 1.0f - 2.0f * (x * x + y * y);
	    float pitch = (float) Math.atan2(t0, t1);

        float yaw = (float)Math.asin((2 * (x * z + y * w)));

	    float t3 = 2.0f * (w * z - x * y);
	    float t4 = 1.0f - 2.0f * (y * y + z * z);
        float roll = (float) Math.atan2(t3, t4);

        roll = (float)Math.toDegrees(roll);
        pitch = (float)Math.toDegrees(pitch);
        yaw = (float)Math.toDegrees(yaw);
        
        Vector3f end = new Vector3f(pitch,yaw,roll);
        
        return end;
    }

    public Matrix4f toMatrix() {
        Quaternionf q = this.normalize();
        return new Matrix4f(
                w*w + x*x - z*z - y*y,    2.0f * q.x * q.y + 2.0f * q.z * q.w,            2.0f * q.x * q.z - 2.0f * q.y * q.w, 0,
                2.0f * q.x * q.y - 2.0f * q.z * q.w,            y*y - z*z + w*w - x*x,     2.0f * q.y * q.z + 2.0f * q.x * q.w, 0,
                2.0f * q.x * q.z + 2.0f * q.y * q.w,            2.0f * q.y * q.z - 2.0f * q.x * q.w,            z*z - y*y - x*x + w*w, 0,
                0,                                              0,                                              0,                                        1);
    }

    public Vector3f transform(Vector3f v){
        return this.toMatrix().transform(new Vector4f(v)).truncate();
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
