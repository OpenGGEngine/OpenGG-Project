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

	public static boolean[] keys = new boolean[65536];
	
	
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		// TODO Auto-generated method stub
		keys[key] = action != GLFW_RELEASE;
	}
	
	
	public static boolean isKeyDown(int keycode) {
		return keys[keycode];
	}
	
}
