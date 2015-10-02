/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.thread;

import com.opengg.core.window.Window;

/**
 *
 * @author Javier
 */
public interface GameThreaded {

    
    public Window w = new Window();
    public void update(long delta);
    public void render();
    public void setup();
    public void end();
}
