/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.math;

import com.opengg.core.math.geom.MinkowskiSet;

/**
 *
 * @author Javier
 */
public class Simplex {
    public MinkowskiSet a, b, c, d;
    public MinkowskiSet recent;
    public Vector3f searchDir;
    public int count = 0;
    public float[] bary = new float[4];
    public boolean contact = false;
    public float distance;

    @Override
    public String toString() {
        return "Simplex{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", d=" + d +
                ", v=" + searchDir +
                ", n=" + count +
                '}';
    }
}
