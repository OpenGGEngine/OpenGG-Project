package com.opengg.core.render.window.awt.window;

import com.opengg.core.exceptions.WindowCreationException;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.render.window.awt.input.AWTKeyboardHandler;
import com.opengg.core.render.window.awt.input.AWTMouseButtonHandler;
import com.opengg.core.render.window.awt.input.AWTMousePosHandler;
import org.lwjgl.opengl.GL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static org.lwjgl.opengl.GL11.*;

public class GGCanvas extends JPanel implements Window {
    private AWTGLCanvas canvas;
    private AWTMousePosHandler mousePosCallback;
    private AWTMouseButtonHandler mouseCallback;
    private AWTKeyboardHandler keyCallback;
    public static Container container;

    @Override
    public void setup(WindowOptions info) {
        GLData data = new GLData();
        data.api = GLData.API.GL;
        data.majorVersion = info.glmajor;
        data.minorVersion = info.glminor;
        data.samples = 1;
        data.redSize = info.rbit;
        data.blueSize = info.bbit;
        data.greenSize = info.gbit;
        data.profile = GLData.Profile.CORE;
        data.swapInterval = info.vsync ? 1 : 0;
        data.forwardCompatible = true;

        canvas = new AWTGLCanvas(data) {
            public void initGL() {
                GL.createCapabilities();
            }
            public void paintGL() {
            }
        };

        this.setLayout(new BorderLayout());
        this.add(canvas);

        canvas.setMinimumSize(new Dimension(info.width, info.height));
        container.add(this);

        canvas.setFocusable(true);
        canvas.requestFocusInWindow();
        canvas.addMouseListener(mouseCallback = new AWTMouseButtonHandler());
        canvas.addKeyListener(keyCallback = new AWTKeyboardHandler());
        canvas.addMouseMotionListener(mousePosCallback = new AWTMousePosHandler());

        var panel = this;

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    panel.getParent().requestFocusInWindow();
                }
            }
        });

        KeyboardController.setHandler(keyCallback);
        MouseController.setPosHandler(mousePosCallback);
        MouseController.setButtonHandler(mouseCallback);
        startFrame();

        if (glGetError() != GL_NO_ERROR) {
            throw new WindowCreationException("OpenGL initialization during window creation failed");
        }

        endFrame();

        canvas.makeContextCurrent();
    }

    @Override
    public void startFrame(){
        canvas.beforeRender();
    }

    @Override
    public void endFrame() {
        canvas.afterRender();
        canvas.swapBuffers();
        canvas.makeContextCurrent();
        mousePosCallback.updateLockMouse();
    }

    @Override
    public float getRatio() {
        return (float)this.getWidth()/(float)this.getHeight();
    }

    @Override
    public boolean shouldClose() {
        return !this.isEnabled();
    }

    @Override
    public void destroy() {
    }

    @Override
    public long getID() {
        return canvas.context;
    }

    @Override
    public int getWidth() {
        return canvas.getWidth();
    }

    @Override
    public int getHeight() {
        return canvas.getHeight();
    }

    @Override
    public boolean getSuccessfulConstruction() {
        return false;
    }

    @Override
    public String getType() {
        return "AWT";
    }

    @Override
    public void setIcon(String path) throws Exception {

    }

    @Override
    public void setVSync(boolean vsync) {

    }

    @Override
    public void setCurrentContext() {
        canvas.makeContextCurrent();
    }

    @Override
    public void setCursorLock(boolean lock) {
        mousePosCallback.setMouseLock(lock);
    }

    public boolean hasFocus(){
        return canvas.hasFocus();
    }
}
