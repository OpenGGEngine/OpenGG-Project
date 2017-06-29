/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.window;

import static com.opengg.core.render.window.WindowOptions.*;

/**
 *
 * @author Javier
 */
public class WindowInfo {
    public int width = 640, 
            height = 480, 
            displaymode = WINDOWED,
            rbit = 8, 
            gbit = 8, 
            bbit = 8, 
            samples = 4;
    public String name= "An OpenGG Application";
    public String type = "GLFW";
    public boolean resizable = false,
            vsync = false;
}
