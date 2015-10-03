/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.input;

/**
 *
 * @author Warren
 */

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

public class KeyBoardHandler extends GLFWKeyCallback{

	static boolean[] keys = new boolean[65536];

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		keys[key] = action != GLFW_RELEASE;
                if(action == GLFW_RELEASE){
                    KeyboardEventHandler.keyReleased(key);
                }else if(action == GLFW_PRESS){
                    KeyboardEventHandler.keyPressed(key);
                }
                
	}
	
	
	public static boolean isKeyDown(int keycode) {
		return keys[keycode];
	}
	
}
