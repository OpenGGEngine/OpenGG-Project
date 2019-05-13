package com.opengg.core.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Vector3big {
    public static final Vector3big identity = new Vector3big();
    private static final long serialVersionUID = 4404184685145307985L;

    public final BigDecimal x;
    public final BigDecimal y;
    public final BigDecimal z;

    /**
     * Creates a default 3d vector with all values set to 0.
     */
    public Vector3big() {
        this.x = new BigDecimal(0f);
        this.y = new BigDecimal(0f);
        this.z = new BigDecimal(0f);
    }

    /**
     * Creates a vector based off of 3 points.
     *
     * @param x
     * @param y
     * @param z
     */
    public Vector3big(BigDecimal x, BigDecimal y, BigDecimal z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3big(BigDecimal val) {
        this.x = val;
        this.y = val;
        this.z = val;
    }

    /**
     * Creates a new vector based off another.
     *
     * @param v Vector to be copied
     */
    public Vector3big(Vector3big v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    /**
     * Creates a new vector based off another.
     *
     * @param v Vector to be copied
     */
    public Vector3big(Vector3f v) {
        this.x = new BigDecimal(v.x);
        this.y = new BigDecimal(v.y);
        this.z = new BigDecimal(v.z);
    }

    /**
     * Returns x value of vector
     * @return x value
     */
    public BigDecimal x(){
        return x;
    }

    /**
     * Returns y value of vector
     * @return y value
     */
    public BigDecimal y(){
        return y;
    }

    /**
     * Returns z value of vector
     * @return z value
     */
    public BigDecimal z(){
        return z;
    }

    /**
     * Returns the value requested<br>
     * Passing in 0 returns x, 1 returns y, and 2 returns z
     * @param val Value to acquire
     * @return Value requested
     */
    public BigDecimal get(int val){
        switch(val){
            case 0: return x;
            case 1: return y;
            case 2: return z;
            default: return new BigDecimal(0);
        }
    }

    /**
     * Creates a new vector copied from this vector, with the x value set to the parameter
     * @param nx New x value
     * @return New vector
     */
    public Vector3big setX(BigDecimal nx){
        return new Vector3big(nx,y,z);
    }

    /**
     * Creates a new vector copied from this vector, with the y value set to the parameter
     * @param ny New y value
     * @return New vector
     */
    public Vector3big setY(BigDecimal ny){
        return new Vector3big(x,ny,z);
    }

    /**
     * Creates a new vector copied from this vector, with the z value set to the parameter
     * @param nz New z value
     * @return New vector
     */
    public Vector3big setZ(BigDecimal nz){
        return new Vector3big(x,y,nz);
    }

    /**
     * Adds a vector to a copy of the current vector, and returns the copy
     * @param v Vector to be added
     * @return Sum of two vectors
     */
    public Vector3big add(Vector3big v){
        return new Vector3big(x.add(v.x), y.add(v.y), z.add(v.z));
    }

    public Vector3big add(Vector3big[] v){
        Vector3big sum = new Vector3big(this);
        for(Vector3big n : v)
            sum.add(n);
        return sum;
    }

    /**
     * Adds a BigDecimal to all elements of a copy of this vector, and returns the copy
     * @param f BigDecimal to be added
     * @return Sum of the vector and BigDecimal
     */
    public Vector3big add(BigDecimal f){
        return new Vector3big(x.add(f), y.add(f), z.add(f));
    }

    /**
     * Subtracts a vector from a copy of the current vector, and returns the copy
     * @param v Vector to be subtracted
     * @return Difference between the two vectors
     */
    public Vector3big subtract(Vector3big v){
        return new Vector3big(x.subtract(v.x), y.subtract(v.y), z.subtract(v.z));
    }

    public Vector3big subtract(Vector3big[] v){
        Vector3big diff = new Vector3big(this);
        for(Vector3big n : v)
            diff.subtract(n);
        return diff;
    }

    /**
     * Subtracts a BigDecimal from all elements of a copy of this vector, and returns the copy
     * @param f BigDecimal to be subtracted
     * @return Difference between the vector and BigDecimal
     */
    public Vector3big subtract(BigDecimal f){
        return new Vector3big(x.subtract(f), y.subtract(f), z.subtract(f));
    }

    public Vector3big divide(BigDecimal scalar) {
        return multiply(new BigDecimal(1).divide(scalar, RoundingMode.HALF_DOWN));
    }

    public Vector3big divide(Vector3big vector) {
        //if (scalar == 0) throw new ArithmeticException("Divide by 0");
        return new Vector3big(x.divide(vector.x, RoundingMode.HALF_DOWN), y.divide(vector.y, RoundingMode.HALF_DOWN), z.divide(vector.z, RoundingMode.HALF_DOWN));
    }

    public Vector3big multiply(BigDecimal scalar) {
        return new Vector3big(x.multiply(scalar), y.multiply(scalar), z.multiply(scalar));
    }

    public Vector3big multiply(Vector3big v) {
        return new Vector3big(x.multiply(v.x), y.multiply(v.y), z.multiply(v.z));
    }

    /**
     * Returns the Euclidean distance between this point and the given point
     * @param v Point to get distance to
     * @return Distance to point
     */
    public BigDecimal distanceTo(Vector3big v) {
        return this.distanceToSquared(v).sqrt(MathContext.DECIMAL128);
    }

    /**
     * Returns the square of the Euclidean distance between this point and the given point<br>
     * This method is useful if the distance is needed for comparison purposes only,
     * as it avoids an expensive square root that {@link #distanceTo} uses
     * @param v Point to get distance to
     * @return Square of distance to point
     */
    public BigDecimal distanceToSquared(Vector3big v){
        return this.x.subtract(v.x).pow(2).add(this.y.subtract(v.y).pow(2)).add(this.z.subtract(v.z).pow(2));
    }

    /**
     * Returns the distance between two points
     * @param v1 Point one
     * @param v2 Point two
     * @return Distance between v1 and v2
     */
    public static BigDecimal distance(Vector3big v1, Vector3big v2){
        return v1.distanceTo(v2);
    }

    /**
     * Returns the Euclidean length of this vector
     * @return Length of vector
     */
    public BigDecimal length() {
        return lengthSquared().sqrt(MathContext.DECIMAL128);
    }

    /**
     * Returns the square of the Euclidean length of this vector<br>
     * This method is useful if lengths just need to be compared,
     * as it avoids an expensive square root in {@link #length}
     * @return
     */
    public BigDecimal lengthSquared() {
        return x.multiply(x).add(y.multiply(y)).add(z.multiply(z));
    }

    /**
     * Return the inverse of this vector<br>
     * This is acquired by multiplying all elements by -1
     * @return Inverse vector
     */
    public Vector3big inverse() {
        return this.multiply(new BigDecimal(-1));
    }

    /**
     * Returns the reciprocal of this vector<br>
     * @return Reciprocal
     */
    public Vector3big reciprocal(){
        return new Vector3big(new BigDecimal(1).divide(this.x, RoundingMode.HALF_DOWN), new BigDecimal(1).divide(this.y, RoundingMode.HALF_DOWN), new BigDecimal(1).divide(this.z, RoundingMode.HALF_DOWN));
    }

    /**
     * Returns a normalized version of this Vector3big<br>
     * This normalized vector has a length of 1, and properly scales
     * all elements. Note, since this method in practice is <code>return multiply(1/length());</code>,
     * a zero vector will cause a divide by 0 error
     * @return Normalized vector
     * @exception ArithmeticException Thrown if the vector to normalize is (0,0,0)
     */
    public Vector3big normalize() {
        return multiply(new BigDecimal(1).divide(length(), RoundingMode.HALF_DOWN));
    }

//    /**
//     * Static version of {@link #dot(com.opengg.core.math.Vector3big) }
//     * @param v1 First vector to dot
//     * @param v2 Second vector to dot
//     * @return Dot product of those two vectors
//     */
//    public static BigDecimal dot(Vector3big v1, Vector3big v2){
//        return v1.dot(v2);
//    }
//
//    /**
//     * Returns the dot product of this vector and another<br>
//     * The dot product in this situation is simply <code>x * v.x + y * v.y + z * v.z</code>
//     * @param v Vector to dot this one with
//     * @return Dot product of this vector
//     */
//    public BigDecimal dot(Vector3big v) {
//        return x * v.x + y * v.y + z * v.z;
//    }
//
//    /**
//     * Static version of {@link #cross(com.opengg.core.math.Vector3big) }
//     * @param v1 First element to cross
//     * @param v2 Second element to cross
//     * @return Cross product of vectors
//     */
//    public static Vector3big cross(Vector3big v1, Vector3big v2){
//        return v1.cross(v2);
//    }
//
//    /**
//     * Returns the cross product of this vector and the given vector<br>
//     * The cross product is the vector perpendicular to the plane formed by the two given
//     * vectors, with its length being dependent on the length and angle between the two given vectors
//     * @param v Vector3big to cross this with
//     * @return Cross product of this vector and another
//     */
//    public Vector3big cross(Vector3big v) {
//        return new Vector3big(y * v.z - z * v.y,
//                z * v.x - x * v.z,
//                x * v.y - y * v.x);
//    }

    /**
     * Returns the the absolute value version of this vector<br>
     * In practice, this method returns a vector where
     * all elements of this object are positive
     * @return Absolute value vector
     */
    public Vector3big abs(){
        BigDecimal xx = (x.signum() == -1) ? x.negate() : x;
        BigDecimal yy = (y.signum() == -1) ? y.negate() : y;
        BigDecimal zz = (z.signum() == -1) ? z.negate() : z;
        return new Vector3big(xx,yy,zz);
    }

//    /**
//     * Does a linear interpolation between the two given vectors<br>
//     * The returned vector is percentage t between the first and second vector
//     * @param sv Starting vector
//     * @param other Ending vector
//     * @param t Percentage through interpolation
//     * @return Interpolated vector
//     */
//    public static Vector3big lerp(Vector3big sv, Vector3big other, BigDecimal t ) {
//        BigDecimal xx = sv.x + (other.x - sv.x) * t;
//        BigDecimal xy = sv.y + (other.y - sv.y) * t;
//        BigDecimal xz = sv.z + (other.z - sv.z) * t;
//        return new Vector3big(xx, xy, xz);
//    }
//
//    /**
//     * Returns this vector reflected across the given normal
//     * @param normal Normal to reflect across
//     * @return Reflected normal
//     */
//    public Vector3big reflect(Vector3big normal) {
//        BigDecimal dot = this.dot(normal);
//        BigDecimal xx = x - (dot + dot) * normal.x;
//        BigDecimal xy = y - (dot + dot) * normal.y;
//        BigDecimal xz = z - (dot + dot) * normal.z;
//        return new Vector3big(xx, xy, xz);
//    }


    public Vector3f toVector3f(){
        return new Vector3f(x.floatValue(), y.floatValue(), z.floatValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3big that = (Vector3big) o;

        if (!x.equals(that.x)) return false;
        if (!y.equals(that.y)) return false;
        return z.equals(that.z);

    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        result = 31 * result + z.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return x + ", " + y + ", " + z;
    }
}
