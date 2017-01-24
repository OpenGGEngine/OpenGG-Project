/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.window;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.OpenGG;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glGetError;

/**
 *
 * @author Javier
 */
public class RenderUtil {
    public static void endFrame(){
        OpenGG.window.endFrame();
        if(glGetError() == GL_TRUE){
            GGConsole.error("OpenGL Error!");
        }
    }
    public static void startFrame(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }
}
