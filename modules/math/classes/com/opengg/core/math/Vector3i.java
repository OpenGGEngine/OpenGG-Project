/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import com.opengg.core.system.Allocator;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * 3 component integer immutable vector with linear algebra functions
 * @author Javier
 */
public record Vector3i(int x, int y, int z) implements Serializable{
    public static final Vector3i identity = new Vector3i();
    private static final long serialVersionUID = 4404184685145307985L;

    /**
     * Creates a default 3d vector with all values set to 0.
     */
    public Vector3i() {
        this(0, 0, 0);
    }


    public Vector3i(int val) {
        this(val, val, val);    
    }

    public Vector2i xz() {
        return new Vector2i(x,z);
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
    public Vector3i setX(int nx){
        return new Vector3i(nx,y,z);
    }

    /**
     * Creates a new vector copied from this vector, with the y value set to the parameter
     * @param ny New y value
     * @return New vector
     */
    public Vector3i setY(int ny){
        return new Vector3i(x,ny,z);
    }

    /**
     * Creates a new vector copied from this vector, with the z value set to the parameter
     * @param nz New z value
     * @return New vector
     */
    public Vector3i setZ(int nz){
        return new Vector3i(x,y,nz);
    }

    /**
     * Adds a vector to a copy of the current vector, and returns the copy
     * @param v Vector to be added
     * @return Sum of two vectors
     */
    public Vector3i add(Vector3i v){
        return new Vector3i(x + v.x, y + v.y, z + v.z);
    }

    public Vector3i add(Vector3i[] v){
        Vector3i sum = this;
        for(Vector3i n : v)
             sum.add(n);
        return sum;
    }

    /**
     * Adds a float to all elements of a copy of this vector, and returns the copy
     * @param f Float to be added
     * @return Sum of the vector and float
     */
    public Vector3i add(int f){
        return new Vector3i(x + f, y + f, z + f);
    }

    /**
     * Subtracts a vector from a copy of the current vector, and returns the copy
     * @param v Vector to be subtracted
     * @return Difference between the two vectors
     */
    public Vector3i subtract(Vector3i v){
        return new Vector3i(x - v.x, y - v.y, z - v.z);
    }

    public Vector3i subtract(Vector3i[] v){
        Vector3i diff = this;
        for(Vector3i n : v)
             diff.subtract(n);
        return diff;
    }

    /**
     * Subtracts a float from all elements of a copy of this vector, and returns the copy
     * @param f Float to be subtracted
     * @return Difference between the vector and float
     */
    public Vector3i subtract(int f){
        return new Vector3i(x - f, y - f, z - f);
    }

    public Vector3i divide(int scalar) {
        return multiply(1 / scalar);
    }

    public Vector3i divide(Vector3i vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return new Vector3i(x / vector.x, y / vector.y, z / vector.z);
    }

    public Vector3i multiply(int scalar) {
        return new Vector3i(x * scalar, y * scalar, z * scalar);
    }

    public Vector3i multiply(Vector3i v) {
        return new Vector3i(x * v.x, y * v.y, z * v.z);
    }

    /**
     * Returns the Euclidean distance between this point and the given point
     * @param v Point to get distance to
     * @return Distance to point
     */
    public float distanceTo(Vector3i v) {
        return (float) Math.sqrt(this.distanceToSquared(v));
    }

    /**
     * Returns the square of the Euclidean distance between this point and the given point<br>
     * This method is useful if the distance is needed for comparison purposes only,
     * as it avoids an expensive square root that {@link #distanceTo} uses
     * @param v Point to get distance to
     * @return Square of distance to point
     */
    public float distanceToSquared(Vector3i v){
        return (float) (Math.pow((this.x - v.x), 2) + Math.pow((this.y - v.y), 2) + Math.pow((this.z - v.z), 2));
    }

    /**
     * Returns the distance between two points
     * @param v1 Point one
     * @param v2 Point two
     * @return Distance between v1 and v2
     */
    public static float distance(Vector3i v1, Vector3i v2){
        return v1.distanceTo(v2);
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
    public Vector3i inverse() {
        return new Vector3i(this.x * -1, this.y * -1, this.z * -1);
    }

    /**
     * Returns the reciprocal of this vector<br>
     * @return Reciprocal
     */
    public Vector3i reciprocal(){
        return new Vector3i(1/this.x, 1/this.y, 1/this.z);
    }

    /**
     * Returns the the absolute value version of this vector<br>
     * In practice, this method returns a vector where 
     * all elements of this object are positive
     * @return Absolute value vector
     */
    public Vector3i abs(){
        int xx = (x < 0) ? -x : x;
        int yy = (y < 0) ? -y : y;
        int zz = (z < 0) ? -z : z;
        return new Vector3i(xx,yy,zz);
    }

    public ByteBuffer getByteBuffer(){
        ByteBuffer buffer = Allocator.alloc(3*Integer.BYTES);
        buffer.putInt(x).putInt(y).putInt(z);
        buffer.flip();
        return buffer;
    }

    public ByteBuffer getStackByteBuffer(){
        ByteBuffer buffer = Allocator.stackAlloc(3*Integer.BYTES);
        buffer.putInt(x).putInt(y).putInt(z);
        buffer.flip();
        return buffer;
    }

    public IntBuffer getStackBuffer() {
        return getStackByteBuffer().asIntBuffer();
    }

    public IntBuffer getBuffer() {
        return getByteBuffer().asIntBuffer();
    }

    @Override
    public String toString(){
        return x + ", " + y + ", " + z;
    }
}
