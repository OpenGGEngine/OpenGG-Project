/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

/**
 *
 * @author ethachu19
 */
public class DashMath {

    public static final double epsilon = 0.0001;
    public static final double epsilonSquared = 0.0000001;

    public static double cos2(double radians) {
        while (radians > 2 * Math.PI) {
            radians -= 2 * Math.PI;
        }
        while (radians < 0) {
            radians += 2 * Math.PI;
        }
        if (radians == 2 * Math.PI || radians == 0) {
            return 1;
        }
        if (radians == Math.PI * 3 / 2 || radians == Math.PI / 2) {
            return 0;
        }
        if (radians == Math.PI) {
            return -1;
        }
        if (radians < Math.PI / 2) {
            return Math.cos(radians);
        } else if (radians < Math.PI) {
            return -1 * Math.cos(Math.PI - radians);
        } else if (radians < Math.PI * 3 / 2) {
            return -1 * Math.cos(radians - Math.PI);
        } else {
            return Math.cos(360 - radians);
        }
    }

    public static double sin2(double radians) {
        while (radians > 2 * Math.PI) {
            radians -= 2 * Math.PI;
        }
        while (radians < 0) {
            radians += 2 * Math.PI;
        }
        if (radians == 2 * Math.PI || radians == 0 || radians == Math.PI) {
            return 1;
        }
        if (radians == Math.PI * 3 / 2) {
            return -1;
        }
        if (radians == Math.PI / 2) {
            return 1;
        }
        if (radians < Math.PI / 2) {
            return Math.cos(radians);
        } else if (radians < Math.PI) {
            return Math.cos(Math.PI - radians);
        } else if (radians < Math.PI * 3 / 2) {
            return -1 * Math.cos(radians - Math.PI);
        } else {
            return -1 * Math.cos(360 - radians);
        }
    }

    public static double tan2(double radians) {
        return sin2(radians) / cos2(radians);
    }

    public static boolean equal(double a, double b) {
        return Math.abs(a - b) <= epsilon;
    }
    
    public static boolean equal(float a, float b) {
        return Math.abs(a - b) <= epsilon;
    }

    public static double lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static double snap(float p, float grid) {
        return equal(grid, 0f) ? Math.floor((p + grid * 0.5) * grid) : p;
    }
    public static float clamp(float f, float min, float max){
        if(f < min) f = min;
        if(f > max) f = max;
        return f;
    }
}
