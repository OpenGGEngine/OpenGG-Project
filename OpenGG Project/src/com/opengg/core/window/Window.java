package com.opengg.core.window;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

// Easy access to all the GLFW and OpenGL methods and constants (otherwise you would have to write GLFW.glfw.. every time)

/**
 * Code is based on the following sample code: http://www.lwjgl.org/guide
 */
public class Window {

    // The GLFW error callback: this tells GLFW what to do if things go wrong
    private static GLFWErrorCallback errorCallback;
    // The handle of the GLFW window
    private static long windowID;

    public static void initWindow(int width,int height) {
        // Set the error handling code: all GLFW errors will be printed to the system error stream (just like println)
        errorCallback = Callbacks.errorCallbackPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        // Initialize GLFW:
        int glfwInitializationResult = glfwInit(); // initialize GLFW and store the result (pass or fail)
        if (glfwInitializationResult == GL_FALSE)
            throw new IllegalStateException("GLFW initialization failed");

        // Configure the GLFW window
        windowID = glfwCreateWindow(
                width, height,   // Width and height of the drawing canvas in pixels
                "Display",     // Title of the window
                MemoryUtil.NULL, // Monitor ID to use for fullscreen mode, or NULL to use windowed mode (LWJGL JavaDoc)
                MemoryUtil.NULL); // Window to share resources with, or NULL to not share resources (LWJGL JavaDoc)

        if (windowID == MemoryUtil.NULL)
            throw new IllegalStateException("GLFW window creation failed");

        // User parameters for
//        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // should be window be visible while it's initializing? (use for position)
//        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
//        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // Mac Modern OpenGL
//        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // Mac Modern OpenGL
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // Modern OpenGL
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2); // Mac Modern OpenGL

        glfwMakeContextCurrent(windowID); // Links the OpenGL context of the window to the current thread (GLFW_NO_CURRENT_CONTEXT error)
        glfwSwapInterval(1); // Enable VSync, which effective caps the frame-rate of the application to 60 frames-per-second
        glfwShowWindow(windowID);

        // If you don't add this line, you'll get the following exception:
        //  java.lang.IllegalStateException: There is no OpenGL context current in the current thread.
        GLContext.createFromCurrent(); // Links LWJGL to the OpenGL context

        // Enter the update loop: keep refreshing the window as long as the window isn't closed
        while (glfwWindowShouldClose(windowID) == GL_FALSE) {
            // Clear the contents of the window (try disabling this and resizing the window – fun guaranteed)
            glClear(GL_COLOR_BUFFER_BIT);
            // Swaps the front and back framebuffers, this is a very technical process which you don't necessarily
            // need to understand. You can simply see this method as updating the window contents.
            glfwSwapBuffers(windowID);
            // Polls the user input. This is very important, because it prevents your application from becoming unresponsive
            glfwPollEvents();
        }

        // It's important to release the resources when the program has finished to prevent dreadful memory leaks
        glfwDestroyWindow(windowID);
        // Destroys all remaining windows and cursors (LWJGL JavaDoc)
        glfwTerminate();
    }
}