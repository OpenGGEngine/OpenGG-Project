/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.newobjloader;

/**
 *
 * @author Warren
 */
public class ReflectivityTransmiss {
    public ReflectivityTransmiss(float r, float g, float b){
        rx = r;
        gy = g;
        bz= b;
    }
    public ReflectivityTransmiss(){};
    public boolean isRGB = false;
    public boolean isXYZ = false;
    public double rx;
    public double gy;
    public double bz;

}
