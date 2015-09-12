/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.test;

import com.opengg.core.window.Window;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GLContext;
import com.opengg.core.input.KeyBoardHandler;
import com.opengg.core.input.MousePosHandler;
import static org.lwjgl.glfw.GLFW.*;
/**
 *
 * @author 19coindreauj
 */
public class Test {
    
    static long window;
    
    public static void main(String args[]){
        
        Window win = new Window();
        try {
            window = win.init(1280, 720, "Test", false);
        } catch (Exception ex){ex.printStackTrace();}
        
        loop();
        
        glfwDestroyWindow(window);

        glfwTerminate();

    }
    
    static void loop(){
        MousePosHandler.setup(window);
        
        GL.setCurrent(GLContext.createFromCurrent()); 
        while ( glfwWindowShouldClose(window) == GL_FALSE ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            
            //System.out.println(MousePosHandler.getPos());
            
            if(KeyBoardHandler.isKeyDown(GLFW_KEY_W)){
                System.out.println(MousePosHandler.getPos());
            }
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
}
