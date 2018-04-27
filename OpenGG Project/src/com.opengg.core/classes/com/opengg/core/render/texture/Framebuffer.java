/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.opengg.core.render.texture;

import com.opengg.core.render.internal.opengl.texture.OpenGLFramebuffer;

import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

/**
 *
 * @author Javier
 */
public interface Framebuffer{
    public static final int DEPTH = GL_DEPTH_ATTACHMENT;

    static Framebuffer generateFramebuffer(){
        return new OpenGLFramebuffer();
    }

    void bind();

    void bindToRead();

    void bindToWrite();

    void useEnabledAttachments();

    void useTexture(int attachment, int loc);

    void attachColorTexture(int width, int height, int attachment);

    void attachFloatingPointTexture(int width, int height, int attachment);

    void attachDepthStencilTexture(int width, int height);

    void attachDepthTexture(int width, int height);

    void attachDepthRenderbuffer(int width, int height);

    void attachRenderbuffer(int width, int height, int storage, int attachment);

    void attachTexture(int width, int height, int format, int intformat, int input, int attachment);

    void blitTo(Framebuffer target);

    void blitToWithDepth(Framebuffer target);

    void blitToBack();

    void refresh();

    void enableRendering();

    void restartRendering();

    void disableRendering();

    void checkForCompletion();

    int getWidth();

    int getHeight();
}
