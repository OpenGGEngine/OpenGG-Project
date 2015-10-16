/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

/**
 *
 * @author Javier
 */
public class MainShaderVertex {
    public float x,y,z,r,g,b,a,xn,yn,zn,v,n;
    public MainShaderVertex(float x,float y,float z,float r,float g,float b,float a,float xn,float yn,float zn,float v,float n){
        this.z = z;
        this.x = x;
        this.y = y;
        this.g = g;
        this.r = r;
        this.b = b;
        this.a = a;
        this.xn = xn;
        this.yn = yn;
        this.zn = zn;
        this.v = v;
        this.n = n;
    }
}
