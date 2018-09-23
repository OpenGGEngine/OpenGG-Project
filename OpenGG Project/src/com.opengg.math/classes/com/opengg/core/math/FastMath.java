/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

/**
 * Utility class for math functions<br><br>
 * This includes collision detection, float equality, table based trigonometry, 
 * and other fast implementations of mathematical functions 
 * @author Warren
 */
import com.opengg.core.math.geom.MinkowskiSet;
import com.opengg.core.math.geom.MinkowskiTriangle;
import com.opengg.core.math.geom.Triangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public final class FastMath {

    /**
     * Multiply nanoseconds by this to get seconds
     */
    public static final float nanoToSec = 1 / 1000000000f;

    /**
     * Default rounding error for floats, used in {@link #isEqual(float, float) }
     */
    public static final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    
    /**
     * Pi constant to 7 decimals
     */
    public static final float PI = 3.1415927f;
    
    /**
     * 2 * {@link #PI}
     */
    public static final float PI2 = PI * 2;
    
    /**
     * 0.5 * {@link #PI}
     */
    static final double PIHalf = PI * 0.5;
    
    /**
     * e, the base of the natural logarithm, to 7 decimals
     */
    public static final float E = 2.7182818f;

    static private final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    static private final int SIN_MASK = ~(-1 << SIN_BITS);
    static private final int SIN_COUNT = SIN_MASK + 1;

    static private final float radFull = PI * 2;
    static private final float degFull = 360;
    static private final float radToIndex = SIN_COUNT / radFull;
    static private final float degToIndex = SIN_COUNT / degFull;

    /**
     * Multiply radians by this to get degrees
     */
    public static final float radiansToDegrees = 180f / PI;
    
    /**
     * Shorthand for {@link #radiansToDegrees}
     */
    public static final float radDeg = radiansToDegrees;
    
    /**
     * Multiply degrees by this to get radians
     */
    public static final float degreesToRadians = PI / 180;
    
    /**
     * Shorthand for {@link #degreesToRadians}
     */
    public static final float degRad = degreesToRadians;

    static private class Sin {

        static final float[] table = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++) {
                table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            }
            for (int i = 0; i < 360; i += 90) {
                table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * degreesToRadians);
            }
        }

        private Sin() {
        }
    }

    /**
     * Returns the sine function for the given radians, indexed from a lookup table 
     */
    public static float sin(float radians) {
        return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
    }

    /**
     * Gets the cosine of the given angle given the sine, for efficiency
     * @param sin Sine function of given angle
     * @param angle Angle
     * @return cos(angle) using sine
     */
    public static double cosFromSin(double sin, double angle) {
        //if (Options.FASTMATH)
        //return sin(angle + PIHalf);
        double cos = Math.sqrt(1.0 - sin * sin);
        double a = angle + PIHalf;
        double b = a - (int) (a / PI2) * PI2;
        if (b < 0.0) {
            b = PI2 + b;
        }
        if (b >= PI) {
            return -cos;
        }
        return cos;
    }

    /**
     * Returns the cosine function for the given radians, indexed from a lookup table 
     */
    public static float cos(float radians) {
        return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
    }

    /**
     * RReturns the sine function for the given degrees, indexed from a lookup table 
     */
    public static float sinDeg(float degrees) {
        return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
    }

    /**
     * Returns the cosine function for the given degrees, indexed from a lookup table 
     */
    public static float cosDeg(float degrees) {
        return Sin.table[(int) ((degrees + 90) * degToIndex) & SIN_MASK];
    }

    // ---
    /**
     * Returns atan2 in radians, faster but less accurate than Math.atan2.
     * Average error of 0.00231 radians (0.1323 degrees),
     * largest error of 0.00488 radians (0.2796 degrees).
     */
    public static float atan2(float y, float x) {
        if (x == 0f) {
            if (y > 0f) {
                return PI / 2;
            }
            if (y == 0f) {
                return 0f;
            }
            return -PI / 2;
        }
        final float atan, z = y / x;
        if (Math.abs(z) < 1f) {
            atan = z / (1f + 0.28f * z * z);
            if (x < 0f) {
                return atan + (y < 0f ? -PI : PI);
            }
            return atan;
        }
        atan = PI / 2 - z / (z * z + 0.28f);
        return y < 0f ? atan - PI : atan;
    }

    // ---
    public static Random random = new Random();

    /**
     * Returns a random number between 0 (inclusive) and the specified value
     * (inclusive).
     */
    public static int random(int range) {
        return random.nextInt(range + 1);
    }

    /**
     * Returns a random number between start (inclusive) and end (inclusive).
     */
    public static int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    /**
     * Returns a random number between 0 (inclusive) and the specified value
     * (inclusive).
     */
    public static long random(long range) {
        return (long) (random.nextDouble() * range);
    }

    /**
     * Returns a random number between start (inclusive) and end (inclusive).
     */
    public static long random(long start, long end) {
        return start + (long) (random.nextDouble() * (end - start));
    }

    /**
     * Returns a random boolean value.
     */
    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    /**
     * Returns true if a random value between 0 and 1 is less than the specified
     * value.
     */
    public static boolean randomBoolean(float chance) {
        return FastMath.random() < chance;
    }

    /**
     * Returns random number between 0.0 (inclusive) and 1.0 (exclusive).
     */
    public static float random() {
        return random.nextFloat();
    }

    /**
     * Returns a random number between 0 (inclusive) and the specified value
     * (exclusive).
     */
    public static float random(float range) {
        return random.nextFloat() * range;
    }

    /**
     * Returns a random number between start (inclusive) and end (exclusive).
     */
    public static float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    /**
     * Returns -1 or 1, randomly.
     */
    public static int randomSign() {
        return 1 | (random.nextInt() >> 31);
    }

    /**
     * Returns a triangularly distributed random number between -1.0 (exclusive)
     * and 1.0 (exclusive), where values around zero are
     * more likely.
     * <p>
     * This is an optimized version of
     * {@link #randomTriangular(float, float, float) randomTriangular(-1, 1, 0)}
     */
    public static float randomTriangular() {
        return random.nextFloat() - random.nextFloat();
    }

    /**
     * Returns a triangularly distributed random number between {@code -max}
     * (exclusive) and {@code max} (exclusive), where values
     * around zero are more likely.
     * <p>
     * This is an optimized version of
     * {@link #randomTriangular(float, float, float) randomTriangular(-max, max, 0)}
     * <p>
     * @param max the upper limit
     */
    public static float randomTriangular(float max) {
        return (random.nextFloat() - random.nextFloat()) * max;
    }

    /**
     * Returns a triangularly distributed random number between {@code min}
     * (inclusive) and {@code max} (exclusive), where the
     * {@code mode} argument defaults to the midpoint between the bounds, giving
     * a symmetric distribution.
     * <p>
     * This method is equivalent of
     * {@link #randomTriangular(float, float, float) randomTriangular(min, max, (min + max) * .5f)}
     * <p>
     * @param min the lower limit
     * @param max the upper limit
     */
    public static float randomTriangular(float min, float max) {
        return randomTriangular(min, max, (min + max) * 0.5f);
    }

    /**
     * Returns a triangularly distributed random number between {@code min}
     * (inclusive) and {@code max} (exclusive), where values
     * around {@code mode} are more likely.
     * <p>
     * @param min the lower limit
     * @param max the upper limit
     * @param mode the point around which the values are more likely
     */
    public static float randomTriangular(float min, float max, float mode) {
        float u = random.nextFloat();
        float d = max - min;
        if (u <= (mode - min) / d) {
            return min + (float) Math.sqrt(u * d * (mode - min));
        }
        return max - (float) Math.sqrt((1 - u) * d * (max - mode));
    }

    // ---
    /**
     * Returns the next power of two. Returns the specified value if the value
     * is already a power of two.
     */
    public static int nextPowerOfTwo(int value) {
        if (value == 0) {
            return 1;
        }
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    /**
     * Returns if a value is a perfect power of two, such that an integer x exists where 2^x=value
     * @param value
     * @return 
     */
    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    /**
     * {@code short} version of {@link #clamp(int,int,int)}
     * @see #clamp(int,int,int)
     */
    public static short clamp(short value, short min, short max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * Clamps the given value to the range of {@code [min, max]}<br>
     * If the value is between min and max, it stays the same, otherwise
     * it will be set to {@code min} if it is below {@code min} and {@code max} if it is above {@code max}
     * @param value Value to be clamped
     * @param min Lower bound of range
     * @param max Upper bound of range
     * @return Clamped value
     */
    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * {@code long} version of {@link #clamp(int,int,int)}
     * @see #clamp(int,int,int)
     */
    public static long clamp(long value, long min, long max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * {@code float} version of {@link #clamp(int,int,int)}
     * @see #clamp(int,int,int)
     */
    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * {@code double} version of {@link #clamp(int,int,int)}
     * @see #clamp(int,int,int)
     */
    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    // ---
    /**
     * Linearly interpolates between fromValue to toValue on progress position.
     */
    public static float lerp(float fromValue, float toValue, float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

    /**
     * Linearly interpolates between two angles in radians. Takes into account
     * that angles wrap at two pi and always takes the
     * direction with the smallest delta angle.
     * <p>
     * @param fromRadians start angle in radians
     * @param toRadians target angle in radians
     * @param progress interpolation value in the range [0, 1]
     * @return the interpolated angle in the range [0, PI2[
     */
    public static float lerpAngle(float fromRadians, float toRadians, float progress) {
        float delta = ((toRadians - fromRadians + PI2 + PI) % PI2) - PI;
        return (fromRadians + delta * progress + PI2) % PI2;
    }

    /**
     * Linearly interpolates between two angles in degrees. Takes into account
     * that angles wrap at 360 degrees and always takes
     * the direction with the smallest delta angle.
     * <p>
     * @param fromDegrees start angle in degrees
     * @param toDegrees target angle in degrees
     * @param progress interpolation value in the range [0, 1]
     * @return the interpolated angle in the range [0, 360[
     */
    public static float lerpAngleDeg(float fromDegrees, float toDegrees, float progress) {
        float delta = ((toDegrees - fromDegrees + 360 + 180) % 360) - 180;
        return (fromDegrees + delta * progress + 360) % 360;
    }

    // ---
    static private final int BIG_ENOUGH_INT = 16 * 1024;
    static private final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    static private final double CEIL = 0.9999999;
    static private final double BIG_ENOUGH_CEIL = 16384.999999999996;
    static private final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

    /**
     * Returns the largest integer less than or equal to the specified float.
     * This method will only properly floor floats from
     * -(2^14) to (Float.MAX_VALUE - 2^14).
     */
    public static int floor(float value) {
        return (int) (value + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the largest integer less than or equal to the specified float.
     * This method will only properly floor floats that are
     * positive. Note this method simply casts the float to int.
     */
    public static int floorPositive(float value) {
        return (int) value;
    }

    /**
     * Returns the smallest integer greater than or equal to the specified
     * float. This method will only properly ceil floats from
     * -(2^14) to (Float.MAX_VALUE - 2^14).
     */
    public static int ceil(float value) {
        return (int) (value + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the smallest integer greater than or equal to the specified
     * float. This method will only properly ceil floats that
     * are positive.
     */
    public static int ceilPositive(float value) {
        return (int) (value + CEIL);
    }

    /**
     * Returns the closest integer to the specified float. This method will only
     * properly round floats from -(2^14) to
     * (Float.MAX_VALUE - 2^14).
     */
    public static int round(float value) {
        return (int) (value + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the closest integer to the specified float. This method will only
     * properly round floats that are positive.
     */
    public static int roundPositive(float value) {
        return (int) (value + 0.5f);
    }

    /**
     * Returns true if the value is zero (using the default tolerance as upper
     * bound)
     */
    public static boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * Returns true if the value is zero.
     * <p>
     * @param tolerance represent an upper bound below which the value is
     * considered zero.
     */
    public static boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    /**
     * Returns true if a is nearly equal to b. The function uses the default
     * floating error tolerance.
     * <p>
     * @param a the first value.
     * @param b the second value.
     */
    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * Returns true if a is nearly equal to b.
     * <p>
     * @param a the first value.
     * @param b the second value.
     * @param tolerance represent an upper bound below which the two values are
     * considered equal.
     * @return If a and b are equal
     */
    public static boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    /**
     * @return the logarithm of value with base a
     */
    public static float log(float a, float value) {
        return (float) (Math.log(value) / Math.log(a));
    }

    /**
     * @return the logarithm of value with base 2
     */
    public static float log2(float value) {
        return log(2, value);
    }
    
    /**
     * Returns the closest point on the line given by {@code a, b} to {@code point}
     * 
     * This computes the point of closest approach of the line defined by {@code a, b} to the point {@code point}.
     * If {@code segClamp} is true, however, it will treat the line as a line segment, clamping the closest approach
     * to the segment
     * @param a First point defining the line/line segment
     * @param b Second point defining the line/line segment
     * @param point Point to approach
     * @param segClamp If closest approach should be locked to line segment
     * @return Closest approach to point {@code point}
     */
    public static Vector3f closestPointTo(Vector3f a, Vector3f b, Vector3f point, boolean segClamp){
        Vector3f ap = point.subtract(a);
        Vector3f ab = b.subtract(a);
        float ab2 = ab.dot(ab);
        float ap_ab = ab.dot(ap);
        float t = ap_ab / ab2;
        if (segClamp)
        {
            if (t < 0.0f) t = 0.0f;
            else if (t > 1.0f) t = 1.0f;
        }
        Vector3f closest = a.add(ab.multiply(t));
        return closest;
    }
    
    public static Vector3f[] closestApproach(Vector3f l1a, Vector3f l1b, Vector3f l2a, Vector3f l2b, boolean lock1, boolean lock2){
        Vector3f o1 = l1a;
        Vector3f d1 = l1b.subtract(l1a);
        
        Vector3f o2 = l2a;
        Vector3f d2 = l2b.subtract(l2a);
        float t1 = new Matrix3f(o2.subtract(o1), d2, d1.cross(d2)).determinant() / d1.cross(d2).lengthSquared();
        
        float t2 = new Matrix3f(o2.subtract(o1), d1, d1.cross(d2)).determinant() / d1.cross(d2).lengthSquared();
        if(lock1){
            if (t1 < 0.0f) t1 = 0.0f;
            else if (t1 > 1.0f) t1 = 1.0f;
        }
        
        if(lock2){
            if (t2 < 0.0f) t2 = 0.0f;
            else if (t2 > 1.0f) t2 = 1.0f;
        }
 
        Vector3f[] end = new Vector3f[2];
        end[0] = o1.add(d1.multiply(t1));
        end[1] = o2.add(d2.multiply(t2));
        return end;
    }

    public static boolean isPointInPolygon(Vector2f point, List<Vector2f> points) {
        int j = points.size() - 1 ;
        boolean oddNodes = false;

        for (int i = 0; i < points.size(); i++) {
            if ((points.get(i).y < point.y && points.get(j).y >= point.y
                    ||   points.get(j).y < point.y && points.get(i).y >= point.y)
                    &&  (points.get(i).x <= point.x || points.get(j).x <= point.x)) {

                oddNodes ^= (points.get(i).x
                          + (point.y - points.get(i).y)
                          / (points.get(j).y - points.get(i).y)
                          * (points.get(j).x - points.get(i).x) < point.x);

            }
            j = i;
        }

        return oddNodes;
    }

    public static List<MinkowskiSet> minkowskiSum(List<Vector3f> v1, List<Vector3f> v2){
        List<MinkowskiSet> sum = new ArrayList<>(v1.size()*v2.size());
        
        for(Vector3f vi : v1)
            for(Vector3f vj : v2)
                sum.add(new MinkowskiSet(vi,vj,vi.add(vj)));
        
        return sum;
    }
    
    public static List<MinkowskiSet> minkowskiSum(List<Vector3f> v1, List<Vector3f> v2, Matrix4f m1, Matrix4f m2){    
        List<MinkowskiSet> sum = new ArrayList<>(v1.size()*v2.size());
        
        for(Vector3f vi : v1){
            for(Vector3f vj : v2){
                Vector3f t1 = m1.transform(new Vector4f(vi)).truncate();
                Vector3f t2 = m2.transform(new Vector4f(vj)).truncate();
                sum.add(new MinkowskiSet(t1,t2,t1.add(t2)));
            }
        }
        
        return sum;
    }
    
    public static List<MinkowskiSet> minkowskiDifference(List<Vector3f> v1, List<Vector3f> v2){    
        List<MinkowskiSet> diff = new ArrayList<>(v1.size()*v2.size());
        
        for(Vector3f vi : v1)
            for(Vector3f vj : v2)
                diff.add(new MinkowskiSet(vi,vj,vi.subtract(vj)));
        
        return diff;
    }
    
    public static List<MinkowskiSet> minkowskiDifference(List<Vector3f> v1, List<Vector3f> v2, Matrix4f m1, Matrix4f m2){    
        List<MinkowskiSet> diff = new ArrayList<>(v1.size()*v2.size());
        
        for(Vector3f vi : v1){
            for(Vector3f vj : v2){
                Vector3f t1 = m1.transform(new Vector4f(vi)).truncate();
                Vector3f t2 = m2.transform(new Vector4f(vj)).truncate();
                diff.add(new MinkowskiSet(t1,t2,t1.subtract(t2)));
            }
        }
        
        return diff;
    }
    
    public static Simplex runGJK(List<MinkowskiSet> vecs){
        Simplex s = new Simplex();
        
        s.v = new Vector3f( 1, 0, 0 );
        s.n = 0; 
 
        for( ; ; )
        {
            s.a = getSupport(s.v, vecs);
 
            if( s.a.v.dot(s.v) < 0 )
                return null;
 
            if( updateGJK(s) ){
                return s;
            }  
        }
    }
    
    private static boolean updateGJK(Simplex s){
        switch (s.n) {
            case 0:
                s.b = s.a;
                s.v = s.v.inverse();
                s.n = 1;
                return false;
            case 1:
                s.v = crossABA( s.b.v.subtract(s.a.v), s.a.v.inverse() );
                
                s.c = s.b;
                s.b = s.a;
                s.n = 2;
                return false;
            case 2:
            {
                Vector3f ao = s.a.v.inverse();
                Vector3f ab = s.b.v.subtract(s.a.v);
                Vector3f ac = s.c.v.subtract(s.a.v);
                
                Vector3f abc = ab.cross(ac);
                Vector3f abp = ab.cross(abc);
                
                if (abp.dot(ao) > 0) {
                    
                    s.c = s.b;
                    s.b = s.a;
                    
                    s.v = crossABA(ab, ao);
                    
                    return false;
                }
                
                Vector3f acp = abc.cross(ac);
                
                if (acp.dot(ao) > 0) {
                    s.b = s.a;
                    s.v = crossABA(ac, ao);
                    return false;
                }
                
                if (abc.dot(ao) > 0) {
                    s.d = s.c;
                    s.c = s.b;
                    s.b = s.a;
                    
                    s.v = abc;
                } else {
                    s.d = s.b;
                    s.b = s.a;
                    
                    s.v = abc.inverse();
                }
                
                s.n = 3;
                
                return false;
            }
            case 3:
            {          
                Vector3f ao = s.a.v.inverse();
                
                Vector3f ab = s.b.v.subtract(s.a.v);
                Vector3f ac = s.c.v.subtract(s.a.v);
                Vector3f ad = s.d.v.subtract(s.a.v);
                
                Vector3f abc = ab.cross(ac);
                Vector3f acd = ac.cross(ad);
                Vector3f adb = ad.cross(ab);
                
                Vector3f tmp;
                final int over_abc = 0x1;
                final int over_acd = 0x2;
                final int over_adb = 0x4;
                
                int plane_tests
                        = (abc.dot(ao) > 0 ? over_abc : 0)
                        | (acd.dot(ao) > 0 ? over_acd : 0)
                        | (adb.dot(ao) > 0 ? over_adb : 0);
                
                switch (plane_tests) {
                    case 0:
                        return true;
                        
                    case over_abc:
                        return checkOneFace(s,ab,ac,ad,ao,abc);
                        
                    case over_acd:
                        
                        s.b = s.c;
                        s.c = s.d;
                        
                        ab = ac;
                        ac = ad;
                        
                        abc = acd;
                        
                        return checkOneFace(s,ab,ac,ad,ao,abc);
                        
                    case over_adb:
                        
                        s.c = s.b;
                        s.b = s.d;
                        
                        ac = ab;
                        ab = ad;
                        
                        abc = adb;
                        
                        return checkOneFace(s,ab,ac,ad,ao,abc);
                        
                    case over_abc | over_acd:
                        return checkTwoFaces(s,ab,ac,ad,ao,abc,acd);
                        
                    case over_acd | over_adb:
                        
                        
                        tmp = s.b.v;
                        s.b = s.c;
                        s.c = s.d;
                        s.d.v = tmp;
                        
                        tmp = ab;
                        ab = ac;
                        ac = ad;
                        ad = tmp;
                        
                        abc = acd;
                        acd = adb;
                        
                        return checkTwoFaces(s,ab,ac,ad,ao,abc,acd);
                        
                    case over_adb | over_abc:
                        
                        tmp = s.c.v;
                        s.c = s.b;
                        s.b = s.d;
                        s.d.v = tmp;
                        
                        tmp = ac;
                        ac = ab;
                        ab = ad;
                        ad = tmp;
                        
                        acd = abc;
                        abc = adb;
                        
                        return checkTwoFaces(s,ab,ac,ad,ao,abc,acd);
                        
                    default:
                        return true;
                }
            }
            default:
                break;
        }
        return false;
    }

    //where is the student center
    private static MinkowskiSet getSupport(Vector3f dir, List<MinkowskiSet> vertices){
        float max = Float.NEGATIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < vertices.size(); i++)
        {
            float dot = dir.dot(vertices.get(i).v);
            if (dot > max)
            {
                max = dot;
                index = i;
            }
        }
        return vertices.get(index);
    }

    private static boolean checkOneFace(Simplex s, Vector3f ab, Vector3f ac, Vector3f ad, Vector3f ao, Vector3f abc){
        
        if (abc.cross(ac).dot(ao) > 0) {

            s.b = s.a;

            s.v = crossABA(ac, ao);

            s.n = 2;

            return false;
        }
        return checkOneFacePt2(s,ab,ac,ad,ao,abc);
    }
    
    private static boolean checkOneFacePt2(Simplex s, Vector3f ab, Vector3f ac, Vector3f ad, Vector3f ao, Vector3f abc){
        if (ab.cross(abc).dot(ao) > 0) {

            s.c = s.b;
            s.b = s.a;

            s.v = crossABA(ab, ao);

            s.n = 2;

            return false;
        }

        s.d = s.c;
        s.c = s.b;
        s.b = s.a;

        s.v = abc;

        s.n = 3;

        return false;
    }

    private static boolean checkTwoFaces(Simplex s, Vector3f ab, Vector3f ac, Vector3f ad, Vector3f ao, Vector3f abc, Vector3f acd) {
        if (abc.cross(ac).dot(ao) > 0) {

            s.b = s.c;
            s.c = s.d;

            ab = ac;
            ac = ad;

            abc = acd;
            return checkOneFace(s,ab,ac,ad,ao,abc);
        }

        return checkOneFacePt2(s,ab,ac,ad,ao,abc);
    }

    private static Vector3f crossABA(Vector3f a, Vector3f b) {
        return a.cross(b).cross(a);
    }
    
    public static MinkowskiTriangle runEPA(Simplex s, List<MinkowskiSet> mdif){
        final float EXIT_THRESHOLD = 0.001f;
        final int EXIT_ITERATION_LIMIT = 50;
        int EXIT_ITERATION_CUR = 0;
        List<MinkowskiTriangle> triangles = new LinkedList<>();
        List<MinkowskiEdge> edges = new LinkedList<>();

        triangles.add(new MinkowskiTriangle(s.a, s.b, s.c));
        triangles.add(new MinkowskiTriangle(s.a, s.c, s.d));
        triangles.add(new MinkowskiTriangle(s.a, s.d, s.b));
        triangles.add(new MinkowskiTriangle(s.b, s.d, s.c));

        while (true) {
            if (EXIT_ITERATION_CUR++ >= EXIT_ITERATION_LIMIT) {
                return null;
            }
            // find closest triangle to origin
            MinkowskiTriangle closest = new MinkowskiTriangle(new MinkowskiSet(),new MinkowskiSet(),new MinkowskiSet());
            float entry_cur_dst = Float.MAX_VALUE;
            
            for (MinkowskiTriangle triangle : triangles) {
                float dst = Math.abs(triangle.n.dot(triangle.a.v));
                if (dst < entry_cur_dst) {
                    entry_cur_dst = dst;
                    closest = triangle;
                }
            }

            MinkowskiSet support = getSupport(closest.n, mdif);

            if ((closest.n.dot(support.v) - entry_cur_dst < EXIT_THRESHOLD)) {
                return closest;
            }

            Iterator<MinkowskiTriangle> iterator = triangles.iterator();
            while (iterator.hasNext()) {

                MinkowskiTriangle t = iterator.next();
                if (t.n.dot(support.v.subtract(t.a.v)) > 0) {
                    addEdge(t.a, t.b, edges);
                    addEdge(t.b, t.c, edges);
                    addEdge(t.c, t.a, edges);
                    iterator.remove();
                }
            }

            // create new triangles from the edges in the edge list
            for (MinkowskiEdge edge : edges) {
                triangles.add(new MinkowskiTriangle(support, edge.a, edge.b));
            }

            edges.clear();
        }
    }

    private static void addEdge(MinkowskiSet a, MinkowskiSet b, List<MinkowskiEdge> edges) {
        for (MinkowskiEdge edge : edges) {
            if (edge.a.v.equals(b.v) && edge.b.v.equals(a.v)) {
                edges.remove(edge);
                return;
            }
        }
        edges.add(new MinkowskiEdge(a, b));
    }

    private static Vector2f Sort(Vector2f v) {
        if (v.x > v.y) {
            return new Vector2f(v.y, v.x);
        }
        return v;
    }

    /// <summary>
    /// This edge to edge test is based on Franlin Antonio's gem: "Faster Line Segment Intersection", in Graphics Gems III, pp. 199-202 
    /// </summary>
    private static boolean EdgeEdgeTest(Vector3f v0, Vector3f v1, Vector3f u0, Vector3f u1, int i0, int i1) {
        float Ax, Ay, Bx, By, Cx, Cy, e, d, f;
        Ax = v1.get(i0) - v0.get(i0);
        Ay = v1.get(i1) - v0.get(i1);

        Bx = u0.get(i0) - u1.get(i0);
        By = u0.get(i1) - u1.get(i1);
        Cx = v0.get(i0) - u0.get(i0);
        Cy = v0.get(i1) - u0.get(i1);
        f = Ay * Bx - Ax * By;
        d = By * Cx - Bx * Cy;
        if ((f > 0 && d >= 0 && d <= f) || (f < 0 && d <= 0 && d >= f)) {
            e = Ax * Cy - Ay * Cx;
            if (f > 0) {
                return e >= 0 && e <= f;
            } else {
                return e <= 0 && e >= f;
            }
        }

        return false;
    }

    private static boolean EdgeAgainstTriEdges(Vector3f v0, Vector3f v1, Vector3f u0, Vector3f u1, Vector3f u2, short i0, short i1) {
        // test edge u0,u1 against v0,v1
        if (EdgeEdgeTest(v0, v1, u0, u1, i0, i1)) {
            return true;
        }

        // test edge u1,u2 against v0,v1 
        if (EdgeEdgeTest(v0, v1, u1, u2, i0, i1)) {
            return true;
        }

        // test edge u2,u1 against v0,v1 
        return EdgeEdgeTest(v0, v1, u2, u0, i0, i1);

    }

    private static boolean PointInTri(Vector3f v0, Vector3f u0, Vector3f u1, Vector3f u2, short i0, short i1) {
        float a, b, c, d0, d1, d2;

        a = u1.get(i1) - u0.get(i1);
        b = -(u1.get(i0) - u0.get(i0));
        c = -a * u0.get(i0) - b * u0.get(i1);
        d0 = a * v0.get(i0) + b * v0.get(i1) + c;

        a = u2.get(i1) - u1.get(i1);
        b = -(u2.get(i0) - u1.get(i0));
        c = -a * u1.get(i0) - b * u1.get(i1);
        d1 = a * v0.get(i0) + b * v0.get(i1) + c;

        a = u0.get(i1) - u2.get(i1);
        b = -(u0.get(i0) - u2.get(i0));
        c = -a * u2.get(i0) - b * u2.get(i1);
        d2 = a * v0.get(i0) + b * v0.get(i1) + c;

        if (d0 * d1 > 0.0f) {
            return d0 * d2 > 0.0f;
        }

        return false;
    }

    private static boolean TriTriCoplanar(Vector3f N, Vector3f v0, Vector3f v1, Vector3f v2, Vector3f u0, Vector3f u1, Vector3f u2) {
        short i0, i1;

        // first project onto an axis-aligned plane, that maximizes the area
        // of the triangles, compute indices: i0,i1. 
        Vector3f A = N.abs();
        if (A.x > A.y) {
            if (A.x > A.z) {
                i0 = 1;
                i1 = 2;
            } else {
                i0 = 0;
                i1 = 1;
            }
        } else {
            if (A.z > A.y) {
                i0 = 0;
                i1 = 1;
            } else {
                i0 = 0;
                i1 = 2;
            }
        }

        if (EdgeAgainstTriEdges(v0, v1, u0, u1, u2, i0, i1)) {
            return true;
        }
        if (EdgeAgainstTriEdges(v1, v2, u0, u1, u2, i0, i1)) {
            return true;
        }
        if (EdgeAgainstTriEdges(v2, v0, u0, u1, u2, i0, i1)) {
            return true;
        }

        if (PointInTri(v0, u0, u1, u2, i0, i1)) {
            return true;
        }
        return PointInTri(u0, v0, v1, v2, i0, i1);

    }

    private static float[] ComputeIntervals(float VV0, float VV1, float VV2,
            float D0, float D1, float D2, float D0D1, float D0D2) {
        float A,B,C,X0,X1;
        if (D0D1 > 0.0f) {
            // here we know that D0D2<=0.0 
            // that is D0, D1 are on the same side, D2 on the other or on the plane 
            A = VV2;
            B = (VV0 - VV2) * D2;
            C = (VV1 - VV2) * D2;
            X0 = D2 - D0;
            X1 = D2 - D1;
        } else if (D0D2 > 0.0f) {
            // here we know that d0d1<=0.0 
            A = VV1;
            B = (VV0 - VV1) * D1;
            C = (VV2 - VV1) * D1;
            X0 = D1 - D0;
            X1 = D1 - D2;
        } else if (D1 * D2 > 0.0f || D0 != 0.0f) {
            // here we know that d0d1<=0.0 or that D0!=0.0 
            A = VV0;
            B = (VV1 - VV0) * D0;
            C = (VV2 - VV0) * D0;
            X0 = D0 - D1;
            X1 = D0 - D2;
        } else if (D1 != 0.0f) {
            A = VV1;
            B = (VV0 - VV1) * D1;
            C = (VV2 - VV1) * D1;
            X0 = D1 - D0;
            X1 = D1 - D2;
        } else if (D2 != 0.0f) {
            A = VV2;
            B = (VV0 - VV2) * D2;
            C = (VV1 - VV2) * D2;
            X0 = D2 - D0;
            X1 = D2 - D1;
        } else {
            return null;
        }

        return new float[]{A,B,C,X0,X1};
    }

    /// <summary>
    /// Checks if the triangle V(v0, v1, v2) intersects the triangle U(u0, u1, u2).
    /// </summary>
    /// <param name="v0">Vertex 0 of V</param>
    /// <param name="v1">Vertex 1 of V</param>
    /// <param name="v2">Vertex 2 of V</param>
    /// <param name="u0">Vertex 0 of U</param>
    /// <param name="u1">Vertex 1 of U</param>
    /// <param name="u2">Vertex 2 of U</param>
    /// <returns>Returns <c>true</c> if V intersects U, otherwise <c>false</c></returns>
    public static boolean isIntersecting(Triangle t1, Triangle t2) {
        Vector3f e1, e2;
        Vector3f n1, n2;
        Vector3f dd;
        Vector2f isect1 = new Vector2f(), isect2 = new Vector2f();
        Vector3f v0 = t1.a;
        Vector3f v1 = t1.b;
        Vector3f v2 = t1.c;
        Vector3f u0 = t2.a;
        Vector3f u1 = t2.b;
        Vector3f u2 = t2.c;
        
        float du0, du1, du2, dv0, dv1, dv2, d1, d2;
        float du0du1, du0du2, dv0dv1, dv0dv2;
        float vp0, vp1, vp2;
        float up0, up1, up2;
        float bb, cc, max;

        short index;

        // compute plane equation of triangle(v0,v1,v2) 
        e1 = v1.subtract(v0);
        e2 = v2.subtract(v0);
        n1 = Vector3f.cross(e1, e2);
        d1 = -Vector3f.dot(n1, v0);
        // plane equation 1: N1.X+d1=0 */

        // put u0,u1,u2 into plane equation 1 to compute signed distances to the plane
        du0 = Vector3f.dot(n1, u0) + d1;
        du1 = Vector3f.dot(n1, u1) + d1;
        du2 = Vector3f.dot(n1, u2) + d1;

        // coplanarity robustness check 
        if (isZero(Math.abs(du0))) {
            du0 = 0.0f;
        }
        if (isZero(Math.abs(du1))) {
            du1 = 0.0f;
        }
        if (isZero(Math.abs(du2))) {
            du2 = 0.0f;
        }

        du0du1 = du0 * du1;
        du0du2 = du0 * du2;

        // same sign on all of them + not equal 0 ? 
        if (du0du1 > 0.0f && du0du2 > 0.0f) {
            // no intersection occurs
            return false;
        }

        // compute plane of triangle (u0,u1,u2)
        e1 = u1.subtract(u0);
        e2 = u2.subtract(u0);
        n2 = Vector3f.cross(e1, e2);
        d2 = -Vector3f.dot(n2, u0);

        // plane equation 2: N2.X+d2=0 
        // put v0,v1,v2 into plane equation 2
        dv0 = Vector3f.dot(n2, v0) + d2;
        dv1 = Vector3f.dot(n2, v1) + d2;
        dv2 = Vector3f.dot(n2, v2) + d2;

        if (isZero(Math.abs(dv0))) {
            dv0 = 0.0f;
        }
        if (isZero(Math.abs(dv1))) {
            dv1 = 0.0f;
        }
        if (isZero(Math.abs(dv2))) {
            dv2 = 0.0f;
        }

        dv0dv1 = dv0 * dv1;
        dv0dv2 = dv0 * dv2;

        // same sign on all of them + not equal 0 ? 
        if (dv0dv1 > 0.0f && dv0dv2 > 0.0f) {
            // no intersection occurs
            return false;
        }

        // compute direction of intersection line 
        dd = Vector3f.cross(n1, n2);

        // compute and index to the largest component of D 
        max = Math.abs(dd.x);
        index = 0;
        bb = Math.abs(dd.y);
        cc = Math.abs(dd.z);
        if (bb > max) {
            max = bb;
            index = 1;
        }
        if (cc > max) {
            max = cc;
            index = 2;
        }

        // this is the simplified projection onto L
        vp0 = v0.get(index);
        vp1 = v1.get(index);
        vp2 = v2.get(index);

        up0 = u0.get(index);
        up1 = u1.get(index);
        up2 = u2.get(index);

        // compute interval for triangle 1 
        float[] vals = ComputeIntervals(vp0, vp1, vp2, dv0, dv1, dv2, dv0dv1, dv0dv2);
        if (vals == null) {
            return TriTriCoplanar(n1, v0, v1, v2, u0, u1, u2);
        }
        float a = vals[0];
        float b = vals[1];
        float c = vals[2];
        float x0 = vals[3];
        float x1 = vals[4];
        
        vals = ComputeIntervals(up0, up1, up2, du0, du1, du2, du0du1, du0du2);
        if (vals == null) {
            return TriTriCoplanar(n1, v0, v1, v2, u0, u1, u2);
        }
        
        float d = vals[0];
        float e = vals[1];
        float f = vals[2];
        float y0 = vals[3];
        float y1 = vals[4];

        float xx, yy, xxyy, tmp;
        xx = x0 * x1;
        yy = y0 * y1;
        xxyy = xx * yy;

        tmp = a * xxyy;
        isect1.x(tmp + b * x1 * yy);
        isect1.y(tmp + c * x0 * yy);

        tmp = d * xxyy;
        isect2.x(tmp + e * xx * y1);
        isect2.y(tmp + f * xx * y0);

        isect1 = Sort(isect1);
        isect2 = Sort(isect2);

        return !(isect1.y < isect2.x || isect2.y < isect1.x);
    }

    private static class Edge{
        Vector3f a;
        Vector3f b;
        
        Edge(Vector3f a, Vector3f b){
            this.a = a;
            this.b = b;
        }
    }

    private static class MinkowskiEdge{
        MinkowskiSet a;
        MinkowskiSet b;
        
        MinkowskiEdge(MinkowskiSet a, MinkowskiSet b){
            this.a = a;
            this.b = b;
        }
    }
    
    public static Vector3f toRadians(Vector3f deg){
        return deg.multiply(degRad);
    }
    
    public static Vector3f toDegrees(Vector3f rad){
        return rad.multiply(radDeg);
    }

    private FastMath() {
    }
}
