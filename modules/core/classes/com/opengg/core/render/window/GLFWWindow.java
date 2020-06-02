package com.opengg.core.render.window;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.exceptions.WindowCreationException;
import com.opengg.core.io.input.keyboard.GLFWKeyboardHandler;
import com.opengg.core.io.input.keyboard.IKeyboardHandler;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.mouse.*;

import static com.opengg.core.render.window.WindowOptions.*;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.vulkan.VkInitPackage;
import com.opengg.core.render.internal.vulkan.VkUtil;
import com.opengg.core.render.internal.vulkan.VulkanRenderer;
import com.opengg.core.render.internal.vulkan.VulkanWindow;
import com.opengg.core.system.Allocator;
import com.opengg.core.util.FileUtil;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

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
    public void setup(WindowInfo windowInfo) {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        HEIGHT = windowInfo.height;
        WIDTH = windowInfo.width;

        if (!glfwInit()) {
            throw new WindowCreationException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, windowInfo.resizable ? GL_TRUE : GL_FALSE);
        glfwWindowHint(GLFW_SAMPLES, windowInfo.samples);

        //glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        switch (windowInfo.renderer){
            case OPENGL -> {
                //OpenGL setup
                glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, windowInfo.glmajor);
                glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, windowInfo.glminor);
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
                glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
            }
            case VULKAN -> glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        }

        mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (windowInfo.displaymode == BORDERLESS) {
            glfwWindowHint(GLFW_RED_BITS, mode.redBits());
            glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
            glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
            glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());

            window = glfwCreateWindow(mode.width(), mode.height(), windowInfo.name, NULL, NULL);

            HEIGHT = mode.height();
            WIDTH = mode.width();

            glfwSetWindowPos(
                    window,
                    0,
                    0
            );
        } else if (windowInfo.displaymode == FULLSCREEN) {
            window = glfwCreateWindow(WIDTH, HEIGHT, windowInfo.name, glfwGetPrimaryMonitor(), NULL);
        } else {
            window = glfwCreateWindow(WIDTH, HEIGHT, windowInfo.name, NULL, NULL);
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

        switch (windowInfo.renderer){
            case OPENGL -> {
                glfwMakeContextCurrent(window);
                glfwSwapInterval(windowInfo.vsync ? 1 : 0);

                GL.createCapabilities();
                if (glGetError() == GL_NO_ERROR) {
                    success = true;
                } else {
                    throw new WindowCreationException("OpenGL initialization during window creation failed");
                }
            }
            case VULKAN -> {
                PointerBuffer requiredExtensions = glfwGetRequiredInstanceExtensions();
                if (requiredExtensions == null) {
                    throw new WindowCreationException("Failed to find list of required Vulkan extensions");
                }
                var vulkanWindow = VulkanWindow.createVulkanInstance(requiredExtensions);

                LongBuffer pSurface = memAllocLong(1);
                VkUtil.catchVulkanException(glfwCreateWindowSurface(vulkanWindow.getInstance(), window, null, pSurface));
                final long surface = pSurface.get(0);
                vulkanWindow.setSurface(surface);

                VulkanRenderer.setWindow(vulkanWindow);
            }
        }
        glfwShowWindow(window);
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
        if(RenderEngine.getRendererType() == WindowInfo.RendererType.OPENGL) {
            glfwSwapBuffers(getID());
        }
        glfwPollEvents();
    }

    @Override
    public long getID() {
        return window;
    }

    @Override
    public int getWidth() {
        IntBuffer w = Allocator.stackAllocInt(1);
        IntBuffer h = Allocator.stackAllocInt(1);
        glfwGetFramebufferSize(window, w, h);
        var realw = w.get();
        Allocator.popStack();
        Allocator.popStack();
        return realw;
    }

    @Override
    public int getHeight() {
        IntBuffer w = Allocator.stackAllocInt(1);
        IntBuffer h = Allocator.stackAllocInt(1);
        glfwGetFramebufferSize(window, w, h);
        var realh = h.get();
        Allocator.popStack();
        Allocator.popStack();
        return realh;
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
