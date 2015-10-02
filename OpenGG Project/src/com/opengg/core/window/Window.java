package com.opengg.core.window;
import com.opengg.core.Vector2f;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.opengg.core.input.KeyBoardHandler;

import java.nio.ByteBuffer;
 

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;



public class Window {
    
    int WIDTH = 300;
    int HEIGHT = 300;
    
    public static long window;
    
    GLFWvidmode mode; 
    ByteBuffer vidmode;
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
    // The GLFW error callback: this tells GLFW what to do if things go wrong
    public void Window() {
       System.out.println("Window inited");
    }
    
    
    public long init(int w, int h, String name, DisplayMode m) throws Exception{
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
        System.out.println("5");
        HEIGHT = h;
        WIDTH = w;
        
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");
 
        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        
        
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        
        vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());   
        mode = new GLFWvidmode(glfwGetVideoMode(glfwGetPrimaryMonitor()));
        if(m == DisplayMode.FULLSCREEN_WINDOWED){
            glfwWindowHint(GLFW_RED_BITS, mode.getRedBits());
            glfwWindowHint(GLFW_GREEN_BITS, mode.getGreenBits());
            glfwWindowHint(GLFW_BLUE_BITS, mode.getBlueBits());
            glfwWindowHint(GLFW_REFRESH_RATE, mode.getRefreshRate());
            
            window = glfwCreateWindow(mode.getWidth(), mode.getHeight(), name, NULL, NULL);
            
            glfwSetWindowPos(
                window,
                0,
                0
            );
        }else if(m == DisplayMode.FULLSCREEN){
            window = glfwCreateWindow(WIDTH, HEIGHT, name,  glfwGetPrimaryMonitor(), NULL);
        }else{
        
            window = glfwCreateWindow(WIDTH, HEIGHT, name, NULL, NULL);
            glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
            );
        }
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
 
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new KeyBoardHandler());
 
        // Get the resolution of the primary monitor
        //ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        
        
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        
        //glViewport(0, 0, WIDTH, HEIGHT);
        
        // Make the window visible
        glfwShowWindow(window);
        GL.createCapabilities();
        return window;
    }
    
    public void setResolution(int w, int h){
        WIDTH = w;
        HEIGHT = h;
        glfwSetWindowSize(window, WIDTH, HEIGHT);
        
        glfwSetWindowPos(
            window,
            (GLFWvidmode.width(vidmode) - WIDTH) / 2,
            (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );
               
    }
    
    public void fullscreen(){
        glfwSetWindowSize(window, mode.getWidth(), mode.getHeight());
        glfwSetWindowPos(
            window,
            0,
            0
        );
    }
    
    public void destroy(){
        glfwDestroyWindow(window);
        
        glfwTerminate();
        
        errorCallback.release();
    }
    public boolean shouldClose(long wind){
        if(glfwWindowShouldClose(wind) == GL_FALSE){
            return false;
        }else{
            return true;
        }
    }
    public void update(){
     glClearColor(0.0f, 0.0f, 1.0f, 0.0f);
    }
    public Vector2f getResolution(){
        return new Vector2f(WIDTH, HEIGHT);
    }
    public float getRatio(){
        return(WIDTH/HEIGHT);
    }
    
}