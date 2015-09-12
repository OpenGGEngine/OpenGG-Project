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
    public void setDisplayMode(int width, int height, boolean fullscreen) {
 
      
                if ((Display.getDisplayMode().getWidth() == width) && 
            (Display.getDisplayMode().getHeight() == height) && 
            (Display.isFullscreen() == fullscreen)) {
            return;
        }
         
        try {
            DisplayMode targetDisplayMode = null;
             
            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;
                 
                for (int i=0;i<modes.length;i++) {
                    DisplayMode current = modes[i];
                     
                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }
 
                      
                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
                            (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width,height);
            }
             
            if (targetDisplayMode == null) {
                System.out.println("Window failed with: "+width+"x"+height+" fs="+fullscreen);
                return;
            }
 
            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
             
        } catch (LWJGLException e) {
            System.out.println("Window failed with  "+width+"x"+height+" fullscreen="+fullscreen + e);
        }
    }
    public void start() {
        try {
	   setDisplayMode(600,600,false);
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
