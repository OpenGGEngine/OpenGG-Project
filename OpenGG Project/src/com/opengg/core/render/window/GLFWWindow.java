package com.opengg.core.render.window;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.exceptions.WindowCreationException;
import com.opengg.core.io.input.keyboard.GLFWKeyboardHandler;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.mouse.GLFWMouseButtonHandler;
import com.opengg.core.io.input.mouse.GLFWMousePosHandler;
import com.opengg.core.io.input.mouse.MouseController;
import static com.opengg.core.render.window.WindowOptions.*;
import java.nio.ByteBuffer;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;



public class GLFWWindow implements Window {
    int WIDTH = 300;
    int HEIGHT = 300;
    
    public static long window;
    
    boolean success;
    GLFWVidMode mode; 
    ByteBuffer vidmode;
    
    GLFWErrorCallback       errorCallback;
    GLFWKeyCallback         keyCallback;
    GLFWCursorPosCallback   mouseCallback;
    GLFWMouseButtonCallback mouseButtonCallback;
    
    public GLFWWindow(WindowInfo winfo) {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        HEIGHT = winfo.height;
        WIDTH = winfo.width;

        if (glfwInit() != true)
            throw new WindowCreationException("Unable to initialize GLFW");

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, winfo.resizable ? GL_TRUE : GL_FALSE);
        glfwWindowHint(GLFW_SAMPLES, winfo.samples);


        mode = glfwGetVideoMode(glfwGetPrimaryMonitor());   
        //mode = new GLFWVidMode(glfwGetVideoMode(glfwGetPrimaryMonitor()));
        if(winfo.displaymode == BORDERLESS){
            glfwWindowHint(GLFW_RED_BITS, mode.redBits());
            glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
            glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
            glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());

            window = glfwCreateWindow(mode.width(), mode.height(), winfo.name, NULL, NULL);
            
            HEIGHT = mode.height();
            WIDTH = mode.width();
            
            glfwSetWindowPos(
                window,
                0,
                0
            );
        }else if(winfo.displaymode == FULLSCREEN){
            window = glfwCreateWindow(WIDTH, HEIGHT, winfo.name,  glfwGetPrimaryMonitor(), NULL);
        }else{
            window = glfwCreateWindow(WIDTH, HEIGHT, winfo.name, NULL, NULL);
            glfwSetWindowPos(
                window,
                (mode.width() - WIDTH) / 2,
                (mode.height() - HEIGHT) / 2
            );
        }
        if ( window == NULL )
            throw new WindowCreationException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, keyCallback = new GLFWKeyboardHandler());
        glfwSetCursorPosCallback(window, mouseCallback = new GLFWMousePosHandler());
        glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonHandler());

        KeyboardController.setHandler((GLFWKeyboardHandler) keyCallback);
        MouseController.setButtonHandler((GLFWMouseButtonHandler) mouseButtonCallback);
        MouseController.setPosHandler((GLFWMousePosHandler) mouseCallback);
        
        glfwMakeContextCurrent(window);
        glfwSwapInterval(winfo.vsync ? 1 : 0);

        glfwShowWindow(window);
        GL.createCapabilities();
        if(glGetError() == GL_NO_ERROR){
            success = true;
            OpenGG.window = this;
        }
        else
            throw new WindowCreationException("OpenGL initialization during window creation failed");
           
    }
    
    public void setResolution(int w, int h){
        WIDTH = w;
        HEIGHT = h;
        glfwSetWindowSize(window, WIDTH, HEIGHT);
        
        glfwSetWindowPos(
            window,
            (mode.width() - WIDTH) / 2,
            (mode.height() - HEIGHT) / 2
        );
               
    }
    
    public void fullscreen(){
        glfwSetWindowSize(window, mode.width(), mode.height());
        glfwSetWindowPos(
            window,
            0,
            0
        );
    }
    
    @Override
    public void destroy(){
        glfwDestroyWindow(window);
        
        glfwTerminate();
        
        errorCallback.free();
    }

    @Override
     public boolean shouldClose(){
        return glfwWindowShouldClose(getID());
    }
     
    public void setColor(float r, float g, float b){
     glClearColor(r/255, b/255, g/255, 0);
    }
    
    @Override
    public float getRatio(){
        return(WIDTH/HEIGHT);
    }
    
    public void setSamples(int samples){
        glfwWindowHint(GLFW_SAMPLES, samples);
    }
    
    public double getTime(){
        return glfwGetTime();
    }
    
    public void setCursorLock(boolean locked){
        if(locked){
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        }else{
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    } 
            
    @Override
    public void endFrame(){
        glfwSwapBuffers(getID());
        glfwPollEvents();
    }
    
    @Override
    public long getID(){
        return window;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public boolean getSuccessfulConstruction() {
        return success;
    }

    @Override
    public int getType() {
        return GLFW;
    }
    
}