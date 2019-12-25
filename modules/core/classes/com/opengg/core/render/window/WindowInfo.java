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


    public WindowInfo setWidth(int width) {
        this.width = width;
        return this;
    }

    public WindowInfo setHeight(int height) {
        this.height = height;
        return this;
    }

    public WindowInfo setDisplayMode(int displaymode) {
        this.displaymode = displaymode;
        return this;
    }

    public WindowInfo setRedBitDepth(int rbit) {
        this.rbit = rbit;
        return this;
    }

    public WindowInfo setGreenBitDepth(int gbit) {
        this.gbit = gbit;
        return this;
    }

    public WindowInfo setBlueBitDepth(int bbit) {
        this.bbit = bbit;
        return this;
    }

    public WindowInfo setSamples(int samples) {
        this.samples = samples;
        return this;
    }

    public WindowInfo setMajorVersion(int glmajor) {
        this.glmajor = glmajor;
        return this;
    }

    public WindowInfo setMinorVersion(int glminor) {
        this.glminor = glminor;
        return this;
    }

    public WindowInfo setName(String name) {
        this.name = name;
        return this;
    }

    public WindowInfo setType(String type) {
        this.type = type;
        return this;
    }

    public WindowInfo setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public WindowInfo setVsync(boolean vsync) {
        this.vsync = vsync;
        return this;
    }
}
