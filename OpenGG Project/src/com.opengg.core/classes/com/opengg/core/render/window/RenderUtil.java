/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.window;

import com.opengg.core.render.RenderEngine;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 *
 * @author Javier
 */
public class RenderUtil {
    public static void endFrame(){
        WindowController.getWindow().endFrame();
        RenderEngine.checkForGLErrors();
    }
    
    public static void startFrame(){
        WindowController.getWindow().startFrame();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    private RenderUtil() {
    }
}
