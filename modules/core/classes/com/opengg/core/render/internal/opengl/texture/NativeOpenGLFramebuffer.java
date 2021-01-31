/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.texture;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.system.NativeResource;
import com.opengg.core.system.NativeResourceManager;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL45.*;

/**
 *
 * @author Javier
 */
public class NativeOpenGLFramebuffer implements NativeResource {

    private final int id;
    
    public NativeOpenGLFramebuffer(){
        if(RenderEngine.validateInitialization()) id = -1;
        else id = glCreateFramebuffers();
        NativeResourceManager.registerNativeResource(this);
    }
    
    public void bind(int target){
        if(RenderEngine.validateInitialization()) return;
        glBindFramebuffer(target, id);
    }
    
    public void attachTexture(int attachment, int texture, int layer){
        if(RenderEngine.validateInitialization()) return;
        glNamedFramebufferTexture(id, attachment, texture, layer);
    }
    
    public void attachTexture1D(int attachment, int textarget, int texture, int layer){
        if(RenderEngine.validateInitialization()) return;
        glNamedFramebufferTextureLayer(id, attachment, textarget, texture, layer);
    }
    
    public void attachTexture2D(int attachment, int textarget, int texture, int layer){
        if(RenderEngine.validateInitialization()) return;
        glNamedFramebufferTextureLayer(id, attachment, textarget, texture, layer);
    }
    
    public void attachTextureLayer(int attachment, int texture, int level, int layer){
        if(RenderEngine.validateInitialization()) return;
        glNamedFramebufferTextureLayer(id, attachment, texture, level, layer);
    }
    
    public void attachRenderbuffer(int attachment, int renderbuffer){
        if(RenderEngine.validateInitialization()) return;
        glNamedFramebufferRenderbuffer(id, attachment, GL_RENDERBUFFER, renderbuffer);
    }
    
    public void blit(int otherFramebuffer, int xmin, int ymin, int xmax, int ymax, int xmin2, int ymin2, int xmax2, int ymax2, int attachments, int filter){
        if(RenderEngine.validateInitialization()) return;
        glBlitNamedFramebuffer(id, otherFramebuffer, xmin, ymin, xmax, ymax, xmin2, ymin2, xmax2, ymax2, attachments, filter);
    }

    public void setDrawBuffers(int[] buffers) {
        glNamedFramebufferDrawBuffers(id, buffers);
    }

    public void clear(Vector3f clearColor) {
        glClearNamedFramebufferfv(id, GL_COLOR, 0, new float[]{clearColor.x,clearColor.y,clearColor.z,0});
        glClearNamedFramebufferfv(id, GL_DEPTH, 0, new float[]{1});
    }

    public int checkCompleteness(){
        if(RenderEngine.validateInitialization()) return 0;
        return glCheckNamedFramebufferStatus(id, GL_FRAMEBUFFER);
    }

    public int getId(){
        return id;
    }

    public void delete(){
        if(RenderEngine.validateInitialization()) return;
        glDeleteFramebuffers(id);
    }

    @Override
    public Runnable onDestroy() {
        int nid = id;
        return () -> glDeleteFramebuffers(nid);
    }

}
