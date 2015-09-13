package com.opengg.test;
import com.opengg.core.input.KeyboardEventHandler;
import com.opengg.core.input.KeyboardListener;
import com.opengg.core.input.MousePosHandler;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;
 
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import com.opengg.core.window.*;
public class OpenGGTest implements KeyboardListener{
 
    // We need to strongly reference callback instances.
    static long window;
    Window win = new Window();
    
    
    
    public OpenGGTest() {
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
 
        try {
            
            window = win.init(1280, 720, "New Test", DisplayMode.WINDOWED);
            loop();
 
            // Release window and window callbacks
            glfwDestroyWindow(window);
        }catch (Exception ex){ex.printStackTrace();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            win.destroy();
        }
        
    }
 

    private void loop() {
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
 
    public static void main(String[] args) {
        new OpenGGTest();
    
    }

    @Override
    public void keyPressed(int key) {

    }

    @Override
    public void keyReleased(int key) {

    }
}
