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
        radians = Math.toDegrees(radians);
        while (radians > 360) {
            radians -= 360;
        }
        while (radians < 0) {
            radians += 360;
        }
        if (radians == 360 || radians == 0)
            return 1;
        if (radians == 270 || radians == 90)
            return 0;
        if (radians == 180)
            return -1;
        if (radians < 90)
            return Math.cos(Math.toRadians(radians));
        //else if ()
          return 69;     
    }
    
}
