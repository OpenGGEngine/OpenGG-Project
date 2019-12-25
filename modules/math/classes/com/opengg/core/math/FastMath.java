/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

/**
 * Utility class for math functions<br><br>
 * This includes float equality, table based trigonometry,
 * and other fast implementations of mathematical functions
 *
 * @author Warren
 */

import com.opengg.core.math.geom.MinkowskiSet;
import com.opengg.core.math.geom.MinkowskiTriangle;
import com.opengg.core.math.geom.Ray;

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
     * e, the base of the natural logarithm, to 7 decimals
     */
    public static final float E = 2.7182818f;
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
    /**
     * 0.5 * {@link #PI}
     */
    static final double PIHalf = PI * 0.5;
    static private final int SIN_BITS = 16; // 16KB. Adjust for accuracy.
    static private final int SIN_MASK = ~(-1 << SIN_BITS);
    static private final int SIN_COUNT = SIN_MASK + 1;
    static private final float radFull = PI * 2;
    static private final float degFull = 360;
    static private final float radToIndex = SIN_COUNT / radFull;
    static private final float degToIndex = SIN_COUNT / degFull;
    // ---
    static private final int BIG_ENOUGH_INT = 16 * 1024;
    static private final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    static private final double CEIL = 0.9999999;
    static private final double BIG_ENOUGH_CEIL = 16384.999999999996;
    static private final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;
    public static boolean accuracyMode = true;
    // ---
    public static Random random = new Random();

    // ---

    private FastMath() {
    }

    /**
     * Returns the sine function for the given radians, indexed from a lookup table
     */
    public static float sin(float radians) {
        if (accuracyMode)
            return (float) Math.sin(radians);
        else
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
        if (accuracyMode)
            return (float) Math.cos(radians);
        else
            return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
    }

    /**
     * RReturns the sine function for the given degrees, indexed from a lookup table
     */
    public static float sinDeg(float degrees) {
        if (accuracyMode)
            return (float) Math.sin(degrees * FastMath.degreesToRadians);
        else
            return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
    }

    /**
     * Returns the cosine function for the given degrees, indexed from a lookup table
     */
    public static float cosDeg(float degrees) {
        if (accuracyMode)
            return (float) Math.cos(degrees * FastMath.degreesToRadians);
        else
            return Sin.table[(int) ((degrees + 90) * degToIndex) & SIN_MASK];
    }

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

    // ---

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

    // ---

    /**
     * {@code short} version of {@link #clamp(int, int, int)}
     * @see #clamp(int, int, int)
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
     * {@code long} version of {@link #clamp(int, int, int)}
     * @see #clamp(int, int, int)
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
     * {@code float} version of {@link #clamp(int, int, int)}
     * @see #clamp(int, int, int)
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
     * {@code double} version of {@link #clamp(int, int, int)}
     * @see #clamp(int, int, int)
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
    public static Vector3f closestPointTo(Vector3f a, Vector3f b, Vector3f point, boolean segClamp) {
        Vector3f ap = point.subtract(a);
        Vector3f ab = b.subtract(a);
        float ab2 = ab.dot(ab);
        float ap_ab = ab.dot(ap);
        float t = ap_ab / ab2;
        if (segClamp) {
            if (t < 0.0f) t = 0.0f;
            else if (t > 1.0f) t = 1.0f;
        }
        Vector3f closest = a.add(ab.multiply(t));
        return closest;
    }

    public static Vector3f[] closestApproach(Vector3f l1a, Vector3f l1b, Vector3f l2a, Vector3f l2b, boolean lock1, boolean lock2) {
        Vector3f o1 = l1a;
        Vector3f d1 = l1b.subtract(l1a);

        Vector3f o2 = l2a;
        Vector3f d2 = l2b.subtract(l2a);
        float t1 = new Matrix3f(o2.subtract(o1), d2, d1.cross(d2)).determinant() / d1.cross(d2).lengthSquared();

        float t2 = new Matrix3f(o2.subtract(o1), d1, d1.cross(d2)).determinant() / d1.cross(d2).lengthSquared();
        if (lock1) {
            if (t1 < 0.0f) t1 = 0.0f;
            else if (t1 > 1.0f) t1 = 1.0f;
        }

        if (lock2) {
            if (t2 < 0.0f) t2 = 0.0f;
            else if (t2 > 1.0f) t2 = 1.0f;
        }

        Vector3f[] end = new Vector3f[2];
        end[0] = o1.add(d1.multiply(t1));
        end[1] = o2.add(d2.multiply(t2));
        return end;
    }

    public static Vector3f getRayPlaneIntersection(Ray ray, Vector3f planeNormal, Vector3f planePoint) {
        var rayPos = ray.getPos();
        var rayDir = ray.getDir();
        Vector3f diff = rayPos.subtract(planePoint);
        float prod1 = diff.dot(planeNormal);
        float prod2 = rayDir.dot(planeNormal);
        float prod3 = prod1 / prod2;
        return rayPos.subtract(rayDir.multiply(prod3));
    }

    public static boolean isPointInPolygon(Vector2f point, List<Vector2f> points) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            if ((points.get(i).y > point.y) != (points.get(j).y > point.y) &&
                    (point.x < (points.get(j).x - points.get(i).x) * (point.y - points.get(i).y) / (points.get(j).y - points.get(i).y) + points.get(i).x)) {
                result = !result;
            }
        }
        return result;
    }

    public static Vector3f getClosestPointInPlane(Vector3f checkPoint, Vector3f planeNormal, Vector3f planePoint) {
        return getRayPlaneIntersection(new Ray(checkPoint, planeNormal.inverse()), planeNormal, planePoint);
    }

    public static Vector3f getFarthestInDirection(Vector3f dir, List<Vector3f> vertices) {
        float max = Float.NEGATIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < vertices.size(); i++) {
            float dot = dir.dot(vertices.get(i));
            if (dot > max) {
                max = dot;
                index = i;
            }
        }
        return vertices.get(index);
    }

    public static Vector3f getFarthest(Vector3f point, List<Vector3f> vertices){
        float max = Float.NEGATIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < vertices.size(); i++) {
            float dist2 = vertices.get(i).distanceToSquared(point);
            if (dist2 > max) {
                max = dist2;
                index = i;
            }
        }
        return vertices.get(index);
    }

    public static Vector3f getFarthestFromLine(Tuple<Vector3f, Vector3f> line, List<Vector3f> vertices){
        float max = Float.NEGATIVE_INFINITY;
        int index = 0;
        for (int i = 0; i < vertices.size(); i++) {
            var closestApproach = FastMath.closestPointTo(line.x, line.y, vertices.get(i), false);
            var dist2 = closestApproach.distanceToSquared(vertices.get(i));
            if (dist2 > max) {
                max = dist2;
                index = i;
            }
        }
        return vertices.get(index);
    }

    public static boolean isCollinear(Vector3f a, Vector3f b, Vector3f c){
        var ab = a.cross(b);
        var ac = a.cross(c);
        return ab.cross(ac).lengthSquared() < 0.0001f;
    }

    public static Vector3f toRadians(Vector3f deg) {
        return deg.multiply(degRad);
    }

    public static Vector3f toDegrees(Vector3f rad) {
        return rad.multiply(radDeg);
    }

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

    private static class Edge {
        Vector3f a;
        Vector3f b;

        Edge(Vector3f a, Vector3f b) {
            this.a = a;
            this.b = b;
        }
    }

}
