/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.states;

import com.opengg.core.render.window.GLFWWindow;
import java.io.IOException;

/**
 *
 * @author Warren
 */
public interface State {
    public String getName();
	
    public void init(GLFWWindow window) throws IOException;
	
    public void render(GLFWWindow window, int delta);
	
    public void update(GLFWWindow window, int delta);
	
    public void enter(GLFWWindow window);

    public void leave(GLFWWindow window);
}
