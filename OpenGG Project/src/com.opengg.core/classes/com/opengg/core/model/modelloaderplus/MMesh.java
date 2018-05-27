/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model.modelloaderplus;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author warre
 */
public class MMesh {
    public FloatBuffer vbo;
    public IntBuffer ibo;
    public MBone[] bones;
    public MMesh(FloatBuffer fb, IntBuffer ibo){
        this.vbo = fb;
        this.ibo = ibo;
    }
}
