/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.math;

import com.opengg.core.system.Allocator;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 *
 * @author Javier
 */
public class Vector4f {
    public final float x;
    public final float y;
    public final float z;
    public final float w;
    
    public Vector4f(){
        this(0,0,0,0);
    }
    
    public Vector4f(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vector4f(Vector4f v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = v.w;
    }
    
    public Vector4f(Vector3f v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = 1;
    }

    public Vector4f(Vector3f v, float w){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = w;
    }
   
    /**
     * Returns x value of vector
     * @return x value
     */
    public float x(){
        return x;
    }
    
    /**
     * Returns y value of vector
     * @return y value
     */
    public float y(){
        return y;
    }
    
    /**
     * Returns z value of vector
     * @return z value
     */
    public float z(){
        return z;
    }
    
    /**
     * Returns the value requested<br>
     * Passing in 0 returns x, 1 returns y, and 2 returns z
     * @param val Value to acquire
     * @return Value requested
     */
    public float get(int val){
        return switch (val) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> 0;
        };
    }
    
    /**
     * Creates a new vector copied from this vector, with the x value set to the parameter
     * @param nx New x value
     * @return New vector
     */
    public Vector4f setX(float nx){
        return new Vector4f(nx,y,z,w);
    }
    
    /**
     * Creates a new vector copied from this vector, with the y value set to the parameter
     * @param ny New y value
     * @return New vector
     */
    public Vector4f setY(float ny){
        return new Vector4f(x,ny,z,w);
    }
    
    /**
     * Creates a new vector copied from this vector, with the z value set to the parameter
     * @param nz New z value
     * @return New vector
     */
    public Vector4f setZ(float nz){
        return new Vector4f(x,y,nz,w);
    }
    
    /**
     * Creates a new vector copied from this vector, with the w value set to the parameter
     * @param nw New w value
     * @return New vector
     */
    public Vector4f setW(float nw){
        return new Vector4f(x,y,z,nw);
    }
    
    /**
     * Adds a vector to a copy of the current vector, and returns the copy
     * @param v Vector to be added
     * @return Sum of two vectors
     */
    public Vector4f add(Vector4f v){
        return new Vector4f(x + v.x, y + v.y, z + v.z, w + v.w);
    }
    
    public Vector4f add(Vector4f[] v){
        Vector4f sum = new Vector4f(this);
        for(Vector4f n : v)
             sum.add(n);
        return sum;
    }
    
    /**
     * Adds a float to all elements of a copy of this vector, and returns the copy
     * @param f Float to be added
     * @return Sum of the vector and float
     */
    public Vector4f add(float f){
        return new Vector4f(x + f, y + f, z + f, w + f);
    }
    
    /**
     * Subtracts a vector from a copy of the current vector, and returns the copy
     * @param v Vector to be subtracted
     * @return Difference between the two vectors
     */
    public Vector4f subtract(Vector4f v){
        return new Vector4f(x - v.x, y - v.y, z - v.z, w - v.w);
    }
    
    public Vector4f subtract(Vector4f[] v){
        Vector4f diff = new Vector4f(this);
        for(Vector4f n : v)
             diff.subtract(n);
        return diff;
    }
    
    /**
     * Subtracts a float from all elements of a copy of this vector, and returns the copy
     * @param f Float to be subtracted
     * @return Difference between the vector and float
     */
    public Vector4f subtract(float f){
        return new Vector4f(x - f, y - f, z - f, w - f);
    }
    
    public Vector4f divide(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return multiply(1f / scalar);
    }
    
    public Vector4f divide(Vector4f vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return new Vector4f(x / vector.x, y / vector.y, z / vector.z, w / vector.w);
    }

    public Vector4f multiply(float scalar) {
        return new Vector4f(x * scalar, y * scalar, z * scalar, w * scalar);
    }
    
    public Vector4f multiply(Vector4f v) {
        return new Vector4f(x * v.x, y * v.y, z * v.z, w * v.w );
    }
    
    /**
     * Returns the Euclidean distance between this point and the given point
     * @param v Point to get distance to
     * @return Distance to point
     */
    public float getDistance(Vector4f v) {
        return (float) Math.sqrt(this.getDistanceSquared(v));
    }
    
    /**
     * Returns the square of the Euclidean distance between this point and the given point<br>
     * This method is useful if the distance is needed for comparison purposes only, 
     * as it avoids an expensive square root that {@link #getDistance} uses
     * @param v Point to get distance to
     * @return Square of distance to point
     */
    public float getDistanceSquared(Vector4f v){
        return (float) (Math.pow((this.x - v.x), 2) + Math.pow((this.y - v.y), 2) + Math.pow((this.z - v.z), 2));
    }
    
    /**
     * Returns the distance between two points
     * @param v1 Point one
     * @param v2 Point two
     * @return Distance between v1 and v2
     */
    public static float getDistance(Vector4f v1, Vector4f v2){
        return v1.getDistance(v2);
    }
    
    /**
     * Returns the Euclidean length of this vector
     * @return Length of vector
     */
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }
    
    /**
     * Returns the square of the Euclidean length of this vector<br>
     * This method is useful if lengths just need to be compared,
     * as it avoids an expensive square root in {@link #length}
     * @return 
     */
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }
    
    /**
     * Return the inverse of this vector<br>
     * This is acquired by multiplying all elements by -1
     * @return Inverse vector
     */
    public Vector4f inverse() {
        return new Vector4f(this.x * -1, this.y * -1, this.z * -1, this.w * -1);
    }

    /**
     * Returns the reciprocal of this vector<br>
     * @return Reciprocal
     */
    public Vector4f reciprocal(){
        return new Vector4f(1/this.x, 1/this.y, 1/this.z, 1/this.w);
    }

    /**
     * Returns a normalized version of this Vector4f<br>
     * This normalized vector has a length of 1, and properly scales
     * all elements. Note, since this method in practice is <code>return multiply(1/length());</code>,
     * a zero vector will cause a divide by 0 error
     * @return Normalized vector
     * @exception ArithmeticException Thrown if the vector to normalize is (0,0,0)
     */
    public Vector4f normalize() {
        return multiply(1f/length());
    }

    /**
     * Static version of {@link #dot(com.opengg.core.math.Vector4f) }
     * @param v1 First vector to dot
     * @param v2 Second vector to dot
     * @return Dot product of those two vectors
     */
    public static float dot(Vector4f v1, Vector4f v2){
        return v1.dot(v2);
    }
    
    /**
     * Returns the dot product of this vector and another<br>
     * The dot product in this situation is simply <code>x * v.x + y * v.y + z * v.z</code>
     * @param v Vector to dot this one with
     * @return Dot product of this vector
     */
    public float dot(Vector4f v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    public Vector4f multiply(Matrix4f mat){
        return new Vector4f(mat.m00 * x + mat.m10 * y + mat.m20 * z + mat.m30 * w,
            mat.m01 * x + mat.m11 * y + mat.m21 * z + mat.m31 * w,
            mat.m02 * x + mat.m12 * y + mat.m22 * z + mat.m32 * w,
            mat.m03 * x + mat.m13 * y + mat.m23 * z + mat.m33 * w);
    }

    public ByteBuffer getByteBuffer(){
        ByteBuffer buffer = Allocator.alloc(4*Float.BYTES);
        buffer.putFloat(x).putFloat(y).putFloat(z).putFloat(w);
        buffer.flip();
        return buffer;
    }

    public ByteBuffer getStackByteBuffer(){
        ByteBuffer buffer = Allocator.stackAlloc(4*Float.BYTES);
        buffer.putFloat(x).putFloat(y).putFloat(z).putFloat(w);
        buffer.flip();
        return buffer;
    }

    public FloatBuffer getStackBuffer() {
        return getStackByteBuffer().asFloatBuffer();
    }

    public FloatBuffer getBuffer() {
        return getByteBuffer().asFloatBuffer();
    }
    
    public Vector3f truncate(){
        return new Vector3f(x,y,z);
    }

    public float[] toArray() {
        return new float[]{x,y,z,w};
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Float.floatToIntBits(this.x);
        hash = 83 * hash + Float.floatToIntBits(this.y);
        hash = 83 * hash + Float.floatToIntBits(this.z);
        hash = 83 * hash + Float.floatToIntBits(this.w);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector4f other = (Vector4f) obj;
        if (!FastMath.isEqual(this.x, other.x)) {
            return false;
        }
        if (!FastMath.isEqual(this.y, other.y)) {
            return false;
        }
        if (!FastMath.isEqual(this.z, other.z)) {
            return false;
        }
        return FastMath.isEqual(this.w, other.w);
    }
    
    
    
    @Override
    public String toString(){
        return x + ", " + y + ", " + z + ", " + w; 
    }


}
