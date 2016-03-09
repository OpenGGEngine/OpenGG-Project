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
public class DashMath{
    
    public static double cos2(double radians) {
        while (radians > 2*Math.PI) {
            radians -= 2*Math.PI;
        }
        while (radians < 0) {
            radians += 2*Math.PI;
        }
        if (radians == 2*Math.PI || radians == 0)
            return 1;
        if (radians == Math.PI*3/2 || radians == Math.PI/2)
            return 0;
        if (radians == Math.PI)
            return -1;
        if (radians < Math.PI/2)
            return Math.cos(radians);
        else if (radians < Math.PI)
            return -1 * Math.cos(Math.PI - radians);
        else if (radians < Math.PI*3/2) 
            return -1 * Math.cos(radians-Math.PI);
        else
            return Math.cos(360 - radians);
    }
    
    public static double sin2(double radians) {
        while (radians > 2*Math.PI) {
            radians -= 2*Math.PI;
        }
        while (radians < 0) {
            radians += 2*Math.PI;
        }
        if (radians == 2*Math.PI || radians == 0 || radians == Math.PI)
            return 1;
        if (radians == Math.PI*3/2)
            return -1;
        if(radians == Math.PI/2)
            return 1;
        if (radians < Math.PI/2)
            return Math.cos(radians);
        else if (radians < Math.PI)
            return Math.cos(Math.PI - radians);
        else if (radians < Math.PI*3/2) 
            return -1 * Math.cos(radians-Math.PI);
        else
            return -1 * Math.cos(360 - radians);
    }
}
