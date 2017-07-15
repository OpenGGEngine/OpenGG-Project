/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.input.mouse;

import com.opengg.core.math.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallback;

/**
 *
 * @author Javier
 */
public class GLFWMousePosHandler extends GLFWCursorPosCallback implements IMousePosHandler{
    double x=0,y=0;
    

    @Override
    public void invoke(long window, double xpos, double ypos) {
        x = xpos;
        y = ypos;
    }  
    
    @Override
    public double getX(){
        return x;
    }
    
    @Override
    public double getY(){
        return y;
    }
    
    /**
     * Returns full mouse position.
     * @return Position of mouse, in Vector2f
     */
    @Override
    public Vector2f getPos(){
        return new Vector2f((float)x, (float)y);
    }
    
}
