/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.input.mouse;

import com.opengg.core.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallback;

/**
 *
 * @author Javier
 */
public class MousePosHandler extends GLFWCursorPosCallback{
    static double x=0,y=0;
    

    @Override
    public void invoke(long window, double xpos, double ypos) {
        x = xpos;
        y = ypos;
    }  
    
    public static double getX(){

        return x;
    }
    public static double getY(){

        return y;
    }
    
    /**
     * Returns full mouse position.
     * @return Position of mouse, in Vector2f
     */
    public static Vector2f getPos(){
        return new Vector2f((float)x, (float)y);
    }
    
}
