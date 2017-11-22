/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

/**
 *
 * @author Warren
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public final class FastMath {

    public static final float nanoToSec = 1 / 1000000000f;

    // ---
    public static final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    public static final float PI = 3.1415927f;
    public static final float PI2 = PI * 2;
    static final double PIHalf = PI * 0.5;
    public static final float E = 2.7182818f;

    static private final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    static private final int SIN_MASK = ~(-1 << SIN_BITS);
    static private final int SIN_COUNT = SIN_MASK + 1;

    static private final float radFull = PI * 2;
    static private final float degFull = 360;
    static private final float radToIndex = SIN_COUNT / radFull;
    static private final float degToIndex = SIN_COUNT / degFull;

    /**
     * multiply by this to convert from radians to degrees
     */
    public static final float radiansToDegrees = 180f / PI;
    public static final float radDeg = radiansToDegrees;
    /**
     * multiply by this to convert from degrees to radians
     */
    public static final float degreesToRadians = PI / 180;
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
    }

    /**
     * Returns the sine in radians from a lookup table.
     */
    public static float sin(float radians) {
        return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
    }

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
     * Returns the cosine in radians from a lookup table.
     */
    public static float cos(float radians) {
        return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
    }

    /**
     * Returns the sine in radians from a lookup table.
     */
    public static float sinDeg(float degrees) {
        return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
    }

    /**
     * Returns the cosine in radians from a lookup table.
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

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    // ---
    public static short clamp(short value, short min, short max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static long clamp(long value, long min, long max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

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
    
    //where is the student center
    public static MinkowskiSet getSupport(Vector3f dir, List<MinkowskiSet> vertices){
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
        if(s.n == 0){
            s.b = s.a;
            s.v = s.v.inverse();
            s.n = 1;
            return false;
        }else if(s.n == 1){
            s.v = crossABA( s.b.v.subtract(s.a.v), s.a.v.inverse() );
 
            s.c = s.b;
            s.b = s.a; 
            s.n = 2;
            return false;
        } else if (s.n == 2) {
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
        } else if (s.n == 3) {
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
        return false;
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
            while(iterator.hasNext()) {
                
                MinkowskiTriangle t = iterator.next();
                if (t.n.dot(support.v.subtract(t.a.v)) > 0) {
                    addEdge(t.a,t.b,edges);
                    addEdge(t.b,t.c,edges);
                    addEdge(t.c,t.a,edges);
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
            if(edge.a.v.equals(b.v) && edge.b.v.equals(a.v)) {
                edges.remove(edge);
                return;
            }
        }
        edges.add(new MinkowskiEdge(a,b));
    }

    
    
    private static class Edge{
        Vector3f a;
        Vector3f b;
        
        public Edge(Vector3f a, Vector3f b){
            this.a = a;
            this.b = b;
        }
    }
    
    private static class MinkowskiEdge{
        MinkowskiSet a;
        MinkowskiSet b;
        
        public MinkowskiEdge(MinkowskiSet a, MinkowskiSet b){
            this.a = a;
            this.b = b;
        }
    }
    
    private static class Triangle{
        Vector3f a;
        Vector3f b;
        Vector3f c;
        Vector3f n;
        
        public Triangle(Vector3f a, Vector3f b, Vector3f c){
            this.a = a;
            this.b = b;
            this.c = c;
            n = b.subtract(a).cross(c.subtract(a));
        }
    }
    
    public static Vector3f toRadians(Vector3f deg){
        return deg.multiply(degRad);
    }
    
    public static Vector3f toDegrees(Vector3f rad){
        return rad.multiply(radDeg);
    }
}
