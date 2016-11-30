/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.input.keyboard;

/**
 *
 * @author Warren
 */

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

public class KeyboardHandler extends GLFWKeyCallback{

	static boolean[] keys = new boolean[65536];

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
            try{
		keys[key] = action != GLFW_RELEASE;
                if(action == GLFW_RELEASE){
                    KeyboardEventHandler.keyReleased(key);
                }else if(action == GLFW_PRESS){
                    KeyboardEventHandler.keyPressed(key);
                }
            }catch(ArrayIndexOutOfBoundsException e){
                
            }
                
	}
	
	
	public static boolean isKeyDown(int keycode) {
		return keys[keycode];
	}
	
}
