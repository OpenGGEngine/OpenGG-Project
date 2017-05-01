/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.window;

import com.opengg.core.math.Vector2f;

/**
 *
 * @author Javier
 */
public interface Window {
    public void endFrame();
    public float getRatio();
    public boolean shouldClose();
    public void destroy();
    public long getID();
    public int getWidth();
    public int getHeight();
    public boolean getSuccessfulConstruction();
    public int getType();
    public void setIcon(String path) throws Exception;
    public void setVSync(boolean vsync);
    public Vector2f getMousePosition();
}
