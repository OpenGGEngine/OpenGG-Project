/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.input;

import com.opengg.core.Vector2f;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;

/**
 *
 * @author Javier
 */
public class MousePosHandler{
    static double x=0,y=0;
    private static GLFWCursorPosCallback cursorPosCallback;
    
//    @Override
//    public void invoke(long window, double xpos, double ypos) {
//        x = (int)xpos;
//        y = (int)ypos;
//        System.out.println(xpos);
//    }
    
    public static void setup(long window){
        glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback(){
 
                @Override
                public void invoke(long window, double xpos, double ypos) {
                    x = xpos;
                    y = ypos;
                }
            });

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
