/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.opengg.core.render.texture;

import com.opengg.core.render.internal.opengl.texture.OpenGLFramebuffer;

import java.nio.ByteBuffer;
import java.util.List;

/**
 *
 * @author Javier
 */
public interface Framebuffer{
    int DEPTH = -1;

    static Framebuffer generateFramebuffer(){
        return new OpenGLFramebuffer();
    }

    Texture getTexture(int attachment);

    List<Texture> getTextures();

    void attachColorTexture(int width, int height, int attachment);

    void attachFloatingPointTexture(int width, int height, int attachment);

    void attachDepthStencilTexture(int width, int height);

    void attachDepthTexture(int width, int height);

    void attachDepthCubemap(int width, int height);

    void attachDepthRenderbuffer(int width, int height);

    void attachRenderbuffer(int width, int height, int storage, int attachment);

    void attachColorCubemap(int width, int height, int attachment);

    void blitTo(Framebuffer target);

    void blitToBack();

    void clearFramebuffer();

    void enableRendering(int x1, int y1, int x2, int y2);

    void disableRendering();

    ByteBuffer readData();

    int getWidth();

    int getHeight();
}
