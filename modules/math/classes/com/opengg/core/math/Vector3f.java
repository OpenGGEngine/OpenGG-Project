/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import com.opengg.core.system.Allocator;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * 3 component immutable vector with linear algebra functions
 * @author Javier
 */
public class Vector3f implements Serializable{
    public static final Vector3f identity = new Vector3f();
    private static final long serialVersionUID = 4404184685145307985L;
    
    public final float x;
    public final float y;
    public final float z;

    /**
     * Creates a default 3d vector with all values set to 0.
     */
    public Vector3f() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }

    /**
     * Creates a vector based off of 3 points.
     *
     * @param x
     * @param y
     * @param z
     */
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(float val) {
        this.x = val;
        this.y = val;
        this.z = val;
    }
    
    /**
     * Creates a new vector based off another.
     *
     * @param v Vector to be copied
     */
    public Vector3f(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    /**
     * Creates a new vector based off of another mutable vector
     * @param v Mutable vector to be copied
     */
    public Vector3f(Vector3fm v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    /**
     * Creates a new vector based off of the first 3 elements of a Vector4f
     * @param v Vector to be copied
     */
    public Vector3f(Vector4f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
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

    public Vector2f xz() {
        return new Vector2f(x,z);
    }

    public Vector2f xy() {
        return new Vector2f(x,y);
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
    public Vector3f setX(float nx){
        return new Vector3f(nx,y,z);
    }
    
    /**
     * Creates a new vector copied from this vector, with the y value set to the parameter
     * @param ny New y value
     * @return New vector
     */
    public Vector3f setY(float ny){
        return new Vector3f(x,ny,z);
    }
    
    /**
     * Creates a new vector copied from this vector, with the z value set to the parameter
     * @param nz New z value
     * @return New vector
     */
    public Vector3f setZ(float nz){
        return new Vector3f(x,y,nz);
    }
    
    /**
     * Adds a vector to a copy of the current vector, and returns the copy
     * @param v Vector to be added
     * @return Sum of two vectors
     */
    public Vector3f add(Vector3f v){
        return new Vector3f(x + v.x, y + v.y, z + v.z);
    }
    
    public Vector3f add(Vector3f[] v){
        Vector3f sum = new Vector3f(this);
        for(Vector3f n : v)
             sum.add(n);
        return sum;
    }
    
    /**
     * Adds a float to all elements of a copy of this vector, and returns the copy
     * @param f Float to be added
     * @return Sum of the vector and float
     */
    public Vector3f add(float f){
        return new Vector3f(x + f, y + f, z + f);
    }
    
    /**
     * Subtracts a vector from a copy of the current vector, and returns the copy
     * @param v Vector to be subtracted
     * @return Difference between the two vectors
     */
    public Vector3f subtract(Vector3f v){
        return new Vector3f(x - v.x, y - v.y, z - v.z);
    }
    
    public Vector3f subtract(Vector3f[] v){
        Vector3f diff = new Vector3f(this);
        for(Vector3f n : v)
             diff.subtract(n);
        return diff;
    }
    
    /**
     * Subtracts a float from all elements of a copy of this vector, and returns the copy
     * @param f Float to be subtracted
     * @return Difference between the vector and float
     */
    public Vector3f subtract(float f){
        return new Vector3f(x - f, y - f, z - f);
    }
    
    public Vector3f divide(float scalar) {
        return multiply(1f / scalar);
    }
    
    public Vector3f divide(Vector3f vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return new Vector3f(x / vector.x, y / vector.y, z / vector.z);
    }

    public Vector3f multiply(float scalar) {
        return new Vector3f(x * scalar, y * scalar, z * scalar);
    }

    public Vector3f multiply(float nx, float ny, float nz) {
        return new Vector3f(x * nx, y * ny, z * nz);
    }
    
    public Vector3f multiply(Vector3f v) {
        return new Vector3f(x * v.x, y * v.y, z * v.z);
    }
    
    /**
     * Returns the Euclidean distance between this point and the given point
     * @param v Point to get distance to
     * @return Distance to point
     */
    public float distanceTo(Vector3f v) {
        return (float) Math.sqrt(this.distanceToSquared(v));
    }
    
    /**
     * Returns the square of the Euclidean distance between this point and the given point<br>
     * This method is useful if the distance is needed for comparison purposes only, 
     * as it avoids an expensive square root that {@link #distanceTo} uses
     * @param v Point to get distance to
     * @return Square of distance to point
     */
    public float distanceToSquared(Vector3f v){
        return (float) (Math.pow((this.x - v.x), 2) + Math.pow((this.y - v.y), 2) + Math.pow((this.z - v.z), 2));
    }
    
    /**
     * Returns the distance between two points
     * @param v1 Point one
     * @param v2 Point two
     * @return Distance between v1 and v2
     */
    public static float distance(Vector3f v1, Vector3f v2){
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
    public Vector3f inverse() {
        return new Vector3f(this.x * -1, this.y * -1, this.z * -1);
    }

    /**
     * Returns the reciprocal of this vector<br>
     * @return Reciprocal
     */
    public Vector3f reciprocal(){
        return new Vector3f(1/this.x, 1/this.y, 1/this.z);
    }

    /**
     * Returns a normalized version of this Vector3f<br>
     * This normalized vector has a length of 1, and properly scales
     * all elements. Note, since this method in practice is <code>return multiply(1/length());</code>,
     * a zero vector will cause a divide by 0 error
     * @return Normalized vector
     * @exception ArithmeticException Thrown if the vector to normalize is (0,0,0)
     */
    public Vector3f normalize() {
        return multiply(1f/length());
    }

    /**
     * Static version of {@link #dot(com.opengg.core.math.Vector3f) }
     * @param v1 First vector to dot
     * @param v2 Second vector to dot
     * @return Dot product of those two vectors
     */
    public static float dot(Vector3f v1, Vector3f v2){
        return v1.dot(v2);
    }
    
    /**
     * Returns the dot product of this vector and another<br>
     * The dot product in this situation is simply <code>x * v.x + y * v.y + z * v.z</code>
     * @param v Vector to dot this one with
     * @return Dot product of this vector
     */
    public float dot(Vector3f v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    /**
     * Static version of {@link #cross(com.opengg.core.math.Vector3f) }
     * @param v1 First element to cross
     * @param v2 Second element to cross
     * @return Cross product of vectors
     */
    public static Vector3f cross(Vector3f v1, Vector3f v2){
        return v1.cross(v2);
    }
    
    /**
     * Returns the cross product of this vector and the given vector<br>
     * The cross product is the vector perpendicular to the plane formed by the two given
     * vectors, with its length being dependent on the length and angle between the two given vectors
     * @param v Vector3f to cross this with
     * @return Cross product of this vector and another
     */
    public Vector3f cross(Vector3f v) {
        return new Vector3f(y * v.z - z * v.y,
                   z * v.x - x * v.z,
                   x * v.y - y * v.x);
    }

    /**
     * Returns the the absolute value version of this vector<br>
     * In practice, this method returns a vector where 
     * all elements of this object are positive
     * @return Absolute value vector
     */
    public Vector3f abs(){
        float xx = (x < 0) ? -x : x;
        float yy = (y < 0) ? -y : y;
        float zz = (z < 0) ? -z : z;
        return new Vector3f(xx,yy,zz);
    }

    /**
     * Finds and replaces all negative zeros in this vector with positive ones
     * @return Vector with all negative zeros removed
     */
    public Vector3f rezero(){
        return new Vector3f(
                x == -0 ? 0 : x,
                y == -0 ? 0 : y,
                z == -0 ? 0 : z
        );
    }
    
    public Vector3f transformByQuat(Quaternionf q){
        return q.transform(this);
    }


    public Vector3f closerToZero(float f){
        return new Vector3f().closerToZeroThis(f);
    }
    
    private Vector3f closerToZeroThis(float f){
        float signX = x < 0 ? -1 : 1;
        float signY = y < 0 ? -1 : 1;
        float signZ = z < 0 ? -1 : 1;
        float xx = (Math.abs(x) - f)*signX;
        float xy = (Math.abs(y) - f)*signY;
        float xz = (Math.abs(z) - f)*signZ;
        return new Vector3f(xx, xy, xz);
    }
    
    public Vector3f closerToZero(Vector3f v){
        return new Vector3f().closerToZeroThis(v);
    }
    
    private Vector3f closerToZeroThis(Vector3f v){
        float signX = x < 0 ? -1 : 1;
        float signY = y < 0 ? -1 : 1;
        float signZ = z < 0 ? -1 : 1;
        float xx = (Math.abs(x) - v.x)*signX;
        float xy = (Math.abs(y) - v.y)*signY;
        float xz = (Math.abs(z) - v.z)*signZ;
        return new Vector3f(xx, xy, xz);
    }

    public static FloatBuffer listToBuffer(List<Vector3f> list){
        return listToBuffer(list.toArray(new Vector3f[0]));
    }

    /**
     * Converts the given array of Vector3fs into a {@link FloatBuffer} of size {@code list.size * 3}
     * @param list Vector3f array to convert
     * @return FloatBuffer containing vectors
     */
    public static FloatBuffer listToBuffer(Vector3f... list){
        FloatBuffer f = Allocator.allocFloat(3 * list.length);
        for(Vector3f v : list){ 
           f.put(v.x);
           f.put(v.y);
           f.put(v.z);
        }
        f.flip();
        return f;
    }
    
    /**
     * Does a linear interpolation between the two given vectors<br>
     * The returned vector is percentage t between the first and second vector
     * @param sv Starting vector
     * @param other Ending vector
     * @param t Percentage through interpolation
     * @return Interpolated vector
     */
    public static Vector3f lerp(Vector3f sv, Vector3f other, float t ) {
        float xx = sv.x + (other.x - sv.x) * t;
        float xy = sv.y + (other.y - sv.y) * t;
        float xz = sv.z + (other.z - sv.z) * t;
        return new Vector3f(xx, xy, xz);
    }

    /**
     * Returns this vector reflected across the given normal
     * @param normal Normal to reflect across
     * @return Reflected normal
     */
    public Vector3f reflect(Vector3f normal) {
        float dot = this.dot(normal);
        float xx = x - (dot + dot) * normal.x;
        float xy = y - (dot + dot) * normal.y;
        float xz = z - (dot + dot) * normal.z;
        return new Vector3f(xx, xy, xz);
    }

    public static Vector3f averageOf(List<Vector3f> vectors){
        return averageOf(vectors.toArray(new Vector3f[0]));
    }

    /**
     * Returns the average vector given a list of vectors<br>
     * This gets the average of each element and creates a new vector based off of each average
     * @param vectors List of vectors to average 
     * @return Average of the given list
     */
    public static Vector3f averageOf(Vector3f... vectors){
        if(vectors.length == 0) return identity;
        float nx=0, ny=0, nz=0;
        for(Vector3f v : vectors){
            nx += v.x;
            ny += v.y;
            nz += v.z;
        }
        return new Vector3f(nx/vectors.length, ny/vectors.length, nz/vectors.length);
    }
    
    /**
     * Returns a FloatBuffer containing the vector onto the heap<br>
     * This FloatBuffer is by default allocated into the stack, 
     * containing the elements of the vector in xyz order.
     * Additionally, the buffer is flipped prior to returning.
     * @return FloatBuffer containing the vector
     */
    public FloatBuffer getBuffer() {
        return getByteBuffer().asFloatBuffer();
    }
    
    /**
     * Returns a FloatBuffer containing the vector onto the stack<br>
     * This FloatBuffer is by default allocated into the stack, 
     * containing the elements of the vector in xyz order.
     * Additionally, the buffer is flipped prior to returning.<br>
     * Note, this must be popped eventually by a call to <@code Allocator.stackPop();>
     * @return FloatBuffer containing the vector
     */
    public FloatBuffer getStackBuffer() {
        return getStackByteBuffer().asFloatBuffer();
    }
    
    /**
     * Returns a ByteBuffer containing the vector<br>
     * This ByteBuffer is by default allocated into the stack, 
     * containing the elements of the vector in xyz order.
     * Additionally, the buffer is flipped prior to returning.
     * @return ByteBuffer containing the vector
     */
    public ByteBuffer getByteBuffer() {
        ByteBuffer buffer = Allocator.alloc(12);
        buffer.putFloat(x).putFloat(y).putFloat(z);
        buffer.flip();
        return buffer;
    }

    public ByteBuffer getStackByteBuffer() {
        ByteBuffer buffer = Allocator.stackAlloc(12);
        buffer.putFloat(x).putFloat(y).putFloat(z);
        buffer.flip();
        return buffer;
    }
    
    /**
     * Returns a byte array containing the vector in xyz order
     * @return 
     */
    public byte[] toByteArray(){   
        return ByteBuffer.allocate(12).putFloat(x).putFloat(y).putFloat(z).array();
    }

    /**
     * Returns a byte array containing the vector in xyz order
     * @return
     */
    public byte[] toLittleEndianByteArray(){
        return ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN).putFloat(x).putFloat(y).putFloat(z).array();
    }

    public float[] toFloatArray() {
        return new float[]{x,y,z};
    }
    
    @Override
    public boolean equals(Object ot){
        if(ot instanceof Vector3f v){
            return FastMath.isEqual(v.x, x) && FastMath.isEqual(v.y, y)  && FastMath.isEqual(v.z, z);
        }   
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Float.floatToIntBits(this.x);
        hash = 23 * hash + Float.floatToIntBits(this.y);
        hash = 23 * hash + Float.floatToIntBits(this.z);
        return hash;
    }

    public String toFormattedString(int decimalCount){
        return String.format("%."+decimalCount+"f, %."+decimalCount+"f, %."+decimalCount+"f", x,y,z);
    }

    @Override
    public String toString(){
        return x + ", " + y + ", " + z;
    }

}
