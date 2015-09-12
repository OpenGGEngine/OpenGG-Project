/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.window;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
/**
 *
 * @author warren
 */
public class Window {
    public void start() {
        try {
	    Display.setDisplayMode(new DisplayMode(800,600));
	    Display.create();
	} catch (LWJGLException e) {
	    e.printStackTrace();
	    System.exit(0);
	}
 
	// init OpenGL here
 
	while (!Display.isCloseRequested()) {
 
	    // render OpenGL here
 
	    Display.update();
	}
 
	Display.destroy();
    }
}
