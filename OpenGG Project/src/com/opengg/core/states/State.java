/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.states;

import com.opengg.core.render.window.Window;
import java.io.IOException;

/**
 *
 * @author Warren
 */
public interface State {
    public String getName();
	
    public void init(Window window) throws IOException;
	
    public void render(Window window, int delta);
	
    public void update(Window window, int delta);
	
    public void enter(Window window);

    public void leave(Window window);
}
