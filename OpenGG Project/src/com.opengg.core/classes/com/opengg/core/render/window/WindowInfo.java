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
            samples = 4,
            glmajor = 4,
            glminor = 3;
    public String name= "An OpenGG Application";
    public String type = "GLFW";
    public boolean resizable = false,
            vsync = false;

    public void width(int width){
        this.width = width;
    }

    public void height(int height){
        this.height = height;
    }

    public void displayMode(int displaymode){
        this.displaymode = displaymode;
    }

    public void redBit(int rbit){
        this.rbit = rbit;
    }

    public void greenBit(int gbit){
        this.gbit = gbit;
    }

    public void blueBit(int bbit){
        this.bbit = bbit;
    }

    public void samples(int samples){
        this.samples = samples;
    }

    public void majorVersion(int glmajor){
        this.glmajor = glmajor;
    }

    public void minorVersion(int glminor){
        this.glminor = glminor;
    }

    public void name(String name){
        this.name = name;
    }

    public void type(String type){
        this.type = type;
    }

    public void resizable(boolean resizable){
        this.resizable = resizable;
    }

    public void vsync(boolean vsync){
        this.vsync = vsync;
    }


}
