/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture1D;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 *
 * @author Javier
 */
public class NativeGLFramebuffer {
    int id;
    
    public NativeGLFramebuffer(){
        id = glGenFramebuffers();
    }
    
    public void bind(int target){
        glBindFramebuffer(target, id);
    }
    
    public void unbind(int target){
        glBindFramebuffer(target, id);
    }
    
    public void attachTexture(int attachment, int texture, int layer){
        glFramebufferTexture(GL_FRAMEBUFFER, attachment, texture, layer);
    }
    
    public void attachTexture1D(int attachment, int textarget, int texture, int layer){
        glFramebufferTexture1D(GL_FRAMEBUFFER, attachment, textarget, texture, layer);
    }
    
    public void attachTexture2D(int attachment, int textarget, int texture, int layer){
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, textarget, texture, layer);
    }
    
    public void attachTextureLayer(int attachment, int texture, int level, int layer){
        glFramebufferTexture1D(GL_FRAMEBUFFER, attachment, texture, level, layer);
    }
    
    public void attachRenderbuffer(int attachment, int renderbuffer){
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, renderbuffer);
    }
    
    public void blit(int xmin, int ymin, int xmax, int ymax, int xmin2, int ymin2, int xmax2, int ymax2, int attachments, int filter){
        glBlitFramebuffer(xmin, ymin, xmax, ymax, xmin2, ymin2, xmax2, ymax2, attachments, filter);
    }
    
    public int checkCompleteness(){
        return glCheckFramebufferStatus(GL_FRAMEBUFFER);
    }
    
    public void delete(){
        glDeleteFramebuffers(id);
    }
}
