package com.opengg.core.render.window;
import com.opengg.core.io.input.KeyBoardHandler;
import com.opengg.core.io.input.MousePosHandler;
import com.opengg.core.util.GlobalInfo;
import java.nio.ByteBuffer;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;



public class GLFWWindow implements Window {
    public int SWT = 4;
    public int GLFW = 6;
    
    int WIDTH = 300;
    int HEIGHT = 300;
    
    public static long window;

    GLFWVidMode mode; 
    ByteBuffer vidmode;
    GLFWErrorCallback errorCallback;
    GLFWKeyCallback   keyCallback;
    GLFWCursorPosCallback mouseCallback;

    public GLFWWindow(int w, int h, String name, DisplayMode m) {
       glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        HEIGHT = h;
        WIDTH = w;

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != true )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable


        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        glfwWindowHint(GLFW_SAMPLES, 4);


        mode = glfwGetVideoMode(glfwGetPrimaryMonitor());   
        //mode = new GLFWVidMode(glfwGetVideoMode(glfwGetPrimaryMonitor()));
        if(m == DisplayMode.FULLSCREEN_WINDOWED){
            glfwWindowHint(GLFW_RED_BITS, mode.redBits());
            glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
            glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
            glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());

            window = glfwCreateWindow(mode.width(), mode.height(), name, NULL, NULL);
            
            HEIGHT = mode.height();
            WIDTH = mode.width();
            
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
                (mode.width() - WIDTH) / 2,
                (mode.height() - HEIGHT) / 2
            );
        }
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, keyCallback = new KeyBoardHandler());
        glfwSetCursorPosCallback(window, mouseCallback = new MousePosHandler());

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        //glViewport(0, 0, WIDTH, HEIGHT);

        // Make the window visible
        glfwShowWindow(window);
        
        
        GL.createCapabilities();

        GlobalInfo.window = this;
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
    public boolean shouldClose(long wind){
        if(glfwWindowShouldClose(wind) == true){
            return false;
        }else{
            return true;
        }
    }
    @Override
     public boolean shouldClose(){
        return glfwWindowShouldClose(GlobalInfo.window.getID());
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
    
    public void setCursorLock(boolean locked){
        if(locked){
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        }else{
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    } 
            
    @Override
    public void endFrame(){
        glfwSwapBuffers(GlobalInfo.window.getID());
    }
    
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
    
}