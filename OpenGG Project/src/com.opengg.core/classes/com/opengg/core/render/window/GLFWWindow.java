package com.opengg.core.render.window;

import com.opengg.core.exceptions.WindowCreationException;
import com.opengg.core.io.input.keyboard.GLFWKeyboardHandler;
import com.opengg.core.io.input.keyboard.IKeyboardHandler;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.mouse.*;

import static com.opengg.core.render.window.WindowOptions.*;
import com.opengg.core.system.Allocator;
import com.opengg.core.util.FileUtil;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.Callback;

import static org.lwjgl.system.MemoryUtil.*;

public class GLFWWindow implements Window {

    int WIDTH = 300;
    int HEIGHT = 300;

    long window;

    boolean success;
    GLFWVidMode mode;
    ByteBuffer vidmode;

    GLFWErrorCallback errorCallback;
    GLFWKeyCallback keyCallback;
    GLFWCursorPosCallback mouseCallback;
    GLFWMouseButtonCallback mouseButtonCallback;
    GLFWScrollCallback mouseScrollCallback;

    @Override
    public void setup(WindowInfo winfo) {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        HEIGHT = winfo.height;
        WIDTH = winfo.width;

        if (!glfwInit()) {
            throw new WindowCreationException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, winfo.glmajor);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, winfo.glminor);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, winfo.resizable ? GL_TRUE : GL_FALSE);
        glfwWindowHint(GLFW_SAMPLES, winfo.samples);
        //glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        // may return null if the debug mode is not available

// cleanup

        mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (winfo.displaymode == BORDERLESS) {
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
        } else if (winfo.displaymode == FULLSCREEN) {
            window = glfwCreateWindow(WIDTH, HEIGHT, winfo.name, glfwGetPrimaryMonitor(), NULL);
        } else {
            window = glfwCreateWindow(WIDTH, HEIGHT, winfo.name, NULL, NULL);
            glfwSetWindowPos(
                    window,
                    (mode.width() - WIDTH) / 2,
                    (mode.height() - HEIGHT) / 2
            );
        }
        if (window == NULL) {
            throw new WindowCreationException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, keyCallback = new GLFWKeyboardHandler());
        glfwSetCursorPosCallback(window, mouseCallback = new GLFWMousePositionHandler());
        glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonHandler());
        glfwSetScrollCallback(window, mouseScrollCallback = new GLFWMouseScrollHandler());

        KeyboardController.setHandler((IKeyboardHandler) keyCallback);
        MouseController.setButtonHandler((MouseButtonHandler) mouseButtonCallback);
        MouseController.setPosHandler((MousePositionHandler) mouseCallback);
        MouseController.setScrollHandler((MouseScrollHandler)mouseScrollCallback);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(winfo.vsync ? 1 : 0);

        glfwShowWindow(window);
        GL.createCapabilities();
       // Callback debugProc = GLUtil.setupDebugMessageCallback();
        if (glGetError() == GL_NO_ERROR) {
            success = true;
        } else {
            throw new WindowCreationException("OpenGL initialization during window creation failed");
        }

    }

    public void setResolution(int w, int h) {
        WIDTH = w;
        HEIGHT = h;
        glfwSetWindowSize(window, WIDTH, HEIGHT);

        glfwSetWindowPos(
                window,
                (mode.width() - WIDTH) / 2,
                (mode.height() - HEIGHT) / 2
        );

    }

    public void fullscreen() {
        glfwSetWindowSize(window, mode.width(), mode.height());
        glfwSetWindowPos(
                window,
                0,
                0
        );
    }

    @Override
    public void destroy() {
        glfwDestroyWindow(window);

        glfwTerminate();

        errorCallback.free();
        keyCallback.free();
        mouseButtonCallback.free();
        mouseCallback.free();
    }

    @Override
    public boolean shouldClose() {
        return glfwWindowShouldClose(getID());
    }

    public void setColor(float r, float g, float b) {
        glClearColor(r / 255, b / 255, g / 255, 0);
    }

    @Override
    public float getRatio() {
        return ((float)WIDTH / (float)HEIGHT);
    }

    public void setSamples(int samples) {
        glfwWindowHint(GLFW_SAMPLES, samples);
    }

    public double getTime() {
        return glfwGetTime();
    }

    @Override
    public void setCursorLock(boolean locked) {
        if (locked) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        } else {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    /**
     *
     * @param path
     * @throws Exception
     */
    @Override
    public void setIcon(String path) throws Exception {
        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);

        // Icons
        {
            ByteBuffer icon16;
            ByteBuffer icon32;
            try {
                icon16 = FileUtil.ioResourceToByteBuffer(path, 2048);
                icon32 = FileUtil.ioResourceToByteBuffer(path, 4096);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try (GLFWImage.Buffer icons = GLFWImage.malloc(2)) {
                ByteBuffer pixels16 = STBImage.stbi_load_from_memory(icon16, w, h, comp, 4);
                icons
                        .position(0)
                        .width(w.get(0))
                        .height(h.get(0))
                        .pixels(pixels16);

                ByteBuffer pixels32 = STBImage.stbi_load_from_memory(icon32, w, h, comp, 4);
                icons
                        .position(1)
                        .width(w.get(0))
                        .height(h.get(0))
                        .pixels(pixels32);

                icons.position(0);
                glfwSetWindowIcon(window, icons);

                STBImage.stbi_image_free(pixels32);
                STBImage.stbi_image_free(pixels16);
            }
        }

        memFree(comp);
        memFree(h);
        memFree(w);

    }

    @Override
    public void endFrame() {
        glfwSwapBuffers(getID());
        glfwPollEvents();
    }

    @Override
    public long getID() {
        return window;
    }

    @Override
    public int getWidth() {
        IntBuffer w = Allocator.allocInt(1);
        IntBuffer h = Allocator.allocInt(1);
        glfwGetFramebufferSize(window, w, h);
        return w.get();
    }

    @Override
    public int getHeight() {
        IntBuffer w = Allocator.allocInt(1);
        IntBuffer h = Allocator.allocInt(1);
        glfwGetFramebufferSize(window, w, h);
        return h.get();
    }

    @Override
    public boolean getSuccessfulConstruction() {
        return success;
    }

    @Override
    public String getType() {
        return "GLFW";
    }

    @Override
    public void setVSync(boolean vsync) {
        glfwSwapInterval(vsync ? 1 : 0);
    }

    @Override
    public void setCurrentContext(){
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }
}
