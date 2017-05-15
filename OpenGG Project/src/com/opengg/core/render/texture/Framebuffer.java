/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.OpenGG;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 *
 * @author Javier
 */
public class Framebuffer extends Texture {

    protected int fb;
    protected int depthbuffer;
    protected List<Integer> textures = new ArrayList<>();
    int x, y;

    public void enableColorAttachments() {
        int[] attachments = new int[textures.size()];
        for (int i = 0; i < textures.size(); i++) {
            attachments[i] = GL_COLOR_ATTACHMENT0 + i;
        }
        glDrawBuffers(attachments);
    }

    public void useTexture(int loc, int attachment) {
        glActiveTexture(GL_TEXTURE0 + loc);
        glBindTexture(GL_TEXTURE_2D, textures.get(attachment));
    }

    public void useDepthTexture(int loc) {
        glActiveTexture(GL_TEXTURE0 + loc);
        glBindTexture(GL_TEXTURE_2D, depthbuffer);
    }

    public void blitBuffer(Framebuffer b) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, b.fb);   // Make sure no FBO is set as the draw framebuffer
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fb); // Make sure your multisampled FBO is the read framebuffer
        glBlitFramebuffer(0, 0, x, y,
                0, 0, b.x, b.y,
                GL_COLOR_BUFFER_BIT, GL_LINEAR);
    }

    public void blitToBack() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);   // Make sure no FBO is set as the draw framebuffer
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fb); // Make sure your multisampled FBO is the read framebuffer
        glDrawBuffer(GL_BACK);                       // Set the back buffer as the draw buffer
        glBlitFramebuffer(0, 0, x, y,
                0, 0, OpenGG.window.getWidth(), OpenGG.window.getHeight(),
                GL_COLOR_BUFFER_BIT, GL_LINEAR);
    }

    public void blitWithDepth(Framebuffer b) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, b.fb);   // Make sure no FBO is set as the draw framebuffer
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fb); // Make sure your multisampled FBO is the read framebuffer
        glBlitFramebuffer(0, 0, x, y,
                0, 0, b.x, b.y,
                GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT, GL_NEAREST);
    }

    public static Framebuffer getFramebuffer(int sizex, int sizey) {
        Framebuffer t = new Framebuffer();
        t.setupFramebuffer(sizex, sizey, 1);
        return t;
    }

    public static Framebuffer getFramebuffer(int sizex, int sizey, int attachmentCount) {
        Framebuffer t = new Framebuffer();
        t.setupFramebuffer(sizex, sizey, attachmentCount);
        return t;
    }

    public void addColorTarget(int attachment) {
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, x, y, 0, GL_RGBA,
          //      GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, x, y, 0, GL_RGBA,
                GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + attachment, texture, 0);
        textures.add(texture);
        if (attachment == 0) {
            this.texture = texture;
        }
    }

    public void addDepthStencilTexture() {
        depthbuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthbuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH32F_STENCIL8, x, y, 0, GL_DEPTH_STENCIL,
                GL_UNSIGNED_INT_24_8, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, depthbuffer, 0);
    }

    public void addDepthTexture() {
        depthbuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthbuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, x, y, 0, GL_DEPTH_COMPONENT,
                GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthbuffer, 0);
    }

    public void addDepthTarget() {
        depthbuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthbuffer);
        glRenderbufferStorage(GL_RENDERBUFFER,
                GL_DEPTH_COMPONENT32, x, y);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER,
                GL_DEPTH_ATTACHMENT,
                GL_RENDERBUFFER, depthbuffer);
    }

    public void setupFramebuffer(int sizex, int sizey, int attachmentCount) {
        x = sizex;
        y = sizey;
        fb = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fb);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        for (int i = 0; i < attachmentCount; i++) {
            addColorTarget(i);
        }

        addDepthStencilTexture();

        try {
            int i = glCheckFramebufferStatus(GL_FRAMEBUFFER);
            if (i != GL_FRAMEBUFFER_COMPLETE) {
                if (i == GL_FRAMEBUFFER_UNSUPPORTED) {
                    GGConsole.error("Framebuffer generation failed: Framebuffer unsupported");
                }
                throw new Exception("Buffer failed to generate!");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void startTexRender() {
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, fb);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glViewport(0, 0, x, y);
    }

    public void endTexRender() {
        glFlush();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, OpenGG.window.getWidth(), OpenGG.window.getHeight());
    }
}
