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
public interface Window {
    void setup(WindowInfo info);
    void endFrame();
    float getRatio();
    boolean shouldClose();
    void destroy();
    long getID();
    int getWidth();
    int getHeight();
    boolean getSuccessfulConstruction();
    String getType();
    void setIcon(String path) throws Exception;
    void setVSync(boolean vsync);
    void setCurrentContext();
    default void setCursorLock(boolean lock){}

    default void startFrame(){}
}
