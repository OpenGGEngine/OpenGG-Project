/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

import static org.lwjgl.opengl.GL11.glEnable;

/**
 *
 * @author Javier
 */
public class GLOptions {
    public static void enable(int i){
        glEnable(i);
    }
}
