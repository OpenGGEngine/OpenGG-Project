/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.io.input.mouse;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

/**
 *
 * @author Javier
 */
public class MouseButtonHandler extends GLFWMouseButtonCallback{
    
    static boolean[] buttons = new boolean[65536];
    
    @Override
    public void close(){
        //throw new Exception();
    }

    @Override
    public void invoke(long window, int button, int action, int mods) {
        buttons[button] = action == GLFW_PRESS;
        if(action == GLFW_RELEASE){
            MouseButtonEventHandler.keyReleased(button);
        }else if(action == GLFW_PRESS){
             MouseButtonEventHandler.keyPressed(button);
        }
    }

    @Override
    public void callback(long args) {
        
    }
    
    public static boolean isButtonDown(int keycode) {
	return buttons[keycode];
    }
    
}
