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
    String getName();
	
    void init(GLFWWindow window) throws IOException;
	
    void render(GLFWWindow window, int delta);
	
    void update(GLFWWindow window, int delta);
	
    void enter(GLFWWindow window);

    void leave(GLFWWindow window);
}
