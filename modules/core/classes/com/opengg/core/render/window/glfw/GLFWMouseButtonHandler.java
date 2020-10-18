/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.window.glfw;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import com.opengg.core.io.input.mouse.MouseButtonHandler;
import com.opengg.core.io.input.mouse.MouseController;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

/**
 *
 * @author Javier
 */
public class GLFWMouseButtonHandler extends GLFWMouseButtonCallback implements MouseButtonHandler {
    
    static boolean[] buttons = new boolean[65536];
    
    @Override
    public void close(){
        //throw new Exception();
    }

    @Override
    public void invoke(long window, int button, int action, int mods) {
        buttons[button] = action == GLFW_PRESS;
        if(action == GLFW_RELEASE){
            MouseController.buttonReleased(button);
        }else if(action == GLFW_PRESS){
            MouseController.buttonPressed(button);
        }
    }

    
    @Override
    public boolean isButtonDown(int keycode) {
	return buttons[keycode];
    }
    
}
