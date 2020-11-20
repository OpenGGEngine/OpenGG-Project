package com.opengg.core.render.window.awt.window;

import com.opengg.core.exceptions.WindowCreationException;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.render.window.awt.input.AWTKeyboardHandler;
import com.opengg.core.render.window.awt.input.AWTMouseButtonHandler;
import com.opengg.core.render.window.awt.input.AWTMousePosHandler;
import com.opengg.core.render.window.awt.input.AWTMouseScrollHandler;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import static org.lwjgl.opengl.GL11.*;

public class GGCanvas implements Window {
    private AWTGLCanvas canvas;
    private AWTMousePosHandler mousePosCallback;
    private AWTMouseButtonHandler mouseCallback;
    private AWTKeyboardHandler keyCallback;
    private AWTMouseScrollHandler scrollCallback;
    public static Container container;

    private Vector2f osScaleFactor = new Vector2f(1,1);

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

        var graphicsEnv = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();

        AffineTransform transform = graphicsEnv.getDefaultTransform();

        osScaleFactor = new Vector2f((float)transform.getScaleX(), (float)transform.getScaleY());

        container.setMinimumSize(new Dimension(info.width/2, info.height/2));
        container.setPreferredSize(new Dimension(info.width, info.height));
        container.add(canvas);

        canvas.setFocusable(true);
        canvas.requestFocusInWindow();
        canvas.addMouseListener(mouseCallback = new AWTMouseButtonHandler());
        canvas.addKeyListener(keyCallback = new AWTKeyboardHandler());
        canvas.addMouseMotionListener(mousePosCallback = new AWTMousePosHandler());
        canvas.addMouseWheelListener(scrollCallback = new AWTMouseScrollHandler());

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    canvas.getParent().requestFocusInWindow();
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
        return !canvas.isEnabled();
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
        return (int) (canvas.getWidth() * osScaleFactor.x);
    }

    @Override
    public int getHeight() {
        return (int) (canvas.getHeight() * osScaleFactor.y);
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
    public void setIcon(String path) {

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
