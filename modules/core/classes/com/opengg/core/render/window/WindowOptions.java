/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.window;

/**
 *
 * @author Javier
 */
public class WindowOptions {
    public int width = 640,
            height = 480, 
            rbit = 8,
            gbit = 8, 
            bbit = 8, 
            samples = 4,
            glmajor = 4,
            glminor = 3;
    public DisplayMode displayMode = DisplayMode.WINDOWED;
    public String name= "An OpenGG Application";
    public String type = "GLFW";
    public RendererType renderer = RendererType.OPENGL;
    public boolean resizable = false,
            vsync = false;


    public WindowOptions setWidth(int width) {
        this.width = width;
        return this;
    }

    public WindowOptions setHeight(int height) {
        this.height = height;
        return this;
    }

    public WindowOptions setDisplayMode(DisplayMode displaymode) {
        this.displayMode = displaymode;
        return this;
    }

    public WindowOptions setRedBitDepth(int rbit) {
        this.rbit = rbit;
        return this;
    }

    public WindowOptions setGreenBitDepth(int gbit) {
        this.gbit = gbit;
        return this;
    }

    public WindowOptions setBlueBitDepth(int bbit) {
        this.bbit = bbit;
        return this;
    }

    public WindowOptions setSamples(int samples) {
        this.samples = samples;
        return this;
    }

    public WindowOptions setMajorVersion(int glmajor) {
        this.glmajor = glmajor;
        return this;
    }

    public WindowOptions setMinorVersion(int glminor) {
        this.glminor = glminor;
        return this;
    }

    public WindowOptions setName(String name) {
        this.name = name;
        return this;
    }

    public WindowOptions setType(String type) {
        this.type = type;
        return this;
    }

    public WindowOptions setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public WindowOptions setVsync(boolean vsync) {
        this.vsync = vsync;
        return this;
    }

    public WindowOptions setRenderer(RendererType renderer) {
        this.renderer = renderer;
        return this;
    }

    public enum RendererType{
        OPENGL, VULKAN
    }

    public enum DisplayMode{
        FULLSCREEN, WINDOWED, FULLSCREEN_WINDOWED
    }
}
