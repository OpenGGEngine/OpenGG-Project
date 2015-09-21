/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.test;

import com.opengg.core.Model;
import com.opengg.core.Vector3f;
import com.opengg.core.window.Window;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GLContext;
import com.opengg.core.input.*;
import com.opengg.core.io.ObjLoader;
import com.opengg.core.window.DisplayMode;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import static org.lwjgl.glfw.GLFW.*;
/**
 *
 * @author 19coindreauj
 */
public class Test implements KeyboardListener{
    
    static long window;
    Window win;
    
    public static void main(String args[]){
        new Test();
    }
    public Test(){
        win = new Window();
        try {
            window = win.init(1280, 720, "Test", DisplayMode.WINDOWED);
            
            ObjLoader obj = new ObjLoader();
            Model model = obj.loadTexturedModel("C:\\test.obj");
            for(Vector3f v : model.getVertices()) {
            System.out.println("X is " + v.x);
            System.out.println("Y is " +v.y );
            System.out.println("Z is " +v.z );
        }
        } catch (Exception ex){ex.printStackTrace();}
       
        loop();
        
        win.destroy();

    }
    
    private void loop(){
        
        KeyboardEventHandler.addToPool(this);
        
        MousePosHandler.setup(window);
        
        GL.setCurrent(GLContext.createFromCurrent()); 
        while ( glfwWindowShouldClose(window) == GL_FALSE ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            
              win.update();
              //Blue Screen
            
            glfwSwapBuffers(window);
            glfwPollEvents();
          
        }
    }

    @Override
    public void keyPressed(int key) {
        if(key == GLFW_KEY_ESCAPE){
            close();
            
        }
    }

    @Override
    public void keyReleased(int key) {
        
    }
    
    void close(){
        glfwSetWindowShouldClose(window, 1);
        win.destroy();
    }
}
