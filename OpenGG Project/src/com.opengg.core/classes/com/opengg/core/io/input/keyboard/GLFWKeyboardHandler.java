/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.input.keyboard;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

public class GLFWKeyboardHandler extends GLFWKeyCallback implements IKeyboardHandler{

	boolean[] keys = new boolean[1024];
        
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
            try{
		keys[key] = action != GLFW_RELEASE;
                if(action == GLFW_RELEASE){
                    KeyboardController.keyReleased(key);
                }else if(action == GLFW_PRESS){
                    KeyboardController.keyPressed(key);
                }
            }catch(ArrayIndexOutOfBoundsException e){}
                
	}
	
        @Override
	public boolean isKeyDown(int keycode) {
		return keys[keycode];
	}
	
}
