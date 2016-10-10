/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Warren
 */
public class Mesh {
    
    public Mesh(FloatBuffer vbodata, IntBuffer inddata){
        this(vbodata, inddata, Material.defaultmaterial);
    }
    
    public Mesh(FloatBuffer vbodata, IntBuffer inddata, Material m) {
        this.vbodata = vbodata;
        this.inddata = inddata;
        this.m = m;
    }
    public FloatBuffer vbodata;
    public IntBuffer inddata;
    public Material m;
}
