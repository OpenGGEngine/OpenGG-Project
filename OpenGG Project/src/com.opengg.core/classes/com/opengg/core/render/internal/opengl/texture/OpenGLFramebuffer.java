/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.texture;

import com.opengg.core.console.GGConsole;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.Renderbuffer;
import com.opengg.core.render.texture.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_24_8;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 *
 * @author Javier
 */
public class OpenGLFramebuffer implements Framebuffer{

    NativeOpenGLFramebuffer fb;
    List<Integer> usedAttachments;
    Map<Integer, Texture> textures;
    List<Renderbuffer> renderbuffers;
    int lx, ly;
    
    public OpenGLFramebuffer(){
        fb = new NativeOpenGLFramebuffer();
        refresh();
    }
    
    @Override
    public void bind(){
        fb.bind(GL_FRAMEBUFFER);
    }
    
    @Override
    public void bindToRead(){
        fb.bind(GL_READ_FRAMEBUFFER);
    }
    
    @Override
    public void bindToWrite(){
        fb.bind(GL_DRAW_FRAMEBUFFER);
    }
    
    @Override
    public void useEnabledAttachments(){
        fb.bind(GL_FRAMEBUFFER);
       
        int[] attachments = new int[usedAttachments.size()];
        for(int i = 0; i < attachments.length; i++){
            if(usedAttachments.get(i) == DEPTH) continue;
            attachments[i] = usedAttachments.get(i);
        }

        glDrawBuffers(attachments);
    }
    
    @Override
    public void useTexture(int attachment, int loc){
        if(attachment != DEPTH) attachment = GL_COLOR_ATTACHMENT0 + attachment;
        Texture tex = textures.get(attachment);
        if(tex != null){
            tex.use(loc);
        }
    }
    
    @Override
    public void attachColorTexture(int width, int height, int attachment){
        attachTexture(width, height, GL_RGBA, GL_RGBA8, GL_UNSIGNED_BYTE,  GL_COLOR_ATTACHMENT0 + attachment);
    }
    
    @Override
    public void attachFloatingPointTexture(int width, int height, int attachment){
        attachTexture(width, height, GL_RGBA, GL_RGBA16F, GL_FLOAT, GL_COLOR_ATTACHMENT0 + attachment);
    }
    
    @Override
    public void attachDepthStencilTexture(int width, int height){
        attachTexture(width, height, GL_DEPTH_STENCIL, GL_DEPTH24_STENCIL8, GL_UNSIGNED_INT_24_8, GL_DEPTH_ATTACHMENT);
    }
    
    @Override
    public void attachDepthTexture(int width, int height){
        attachTexture(width, height, GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT32, GL_FLOAT, GL_DEPTH_ATTACHMENT);
    }

    @Override
    public void attachTexture(int width, int height, int format, int intformat, int input, int attachment){
        Texture tex = Texture.get2DFramebufferTexture(width, height, format, intformat, input);

        fb.bind(GL_FRAMEBUFFER);
        fb.attachTexture(attachment, tex.getID(), 0);
        checkForCompletion();
        fb.unbind(GL_FRAMEBUFFER);

        textures.put(attachment, tex);
        usedAttachments.add(attachment);

        if(lx < width) lx = width;
        if(ly < height) ly = height;
    }

    @Override
    public void attachDepthRenderbuffer(int width, int height){
        attachRenderbuffer(width, height, GL_DEPTH_COMPONENT, GL_DEPTH_ATTACHMENT);
    }
    
    @Override
    public void attachRenderbuffer(int width, int height, int storage, int attachment){
        Renderbuffer rb = Renderbuffer.create(width, height, storage);
        fb.bind(GL_FRAMEBUFFER);
        fb.attachRenderbuffer(attachment, rb.getID());
        checkForCompletion();
        
        renderbuffers.add(rb);
        usedAttachments.add(attachment);
        
        if(lx < width) lx = width;
        if(ly < height) ly = height;
    }

    @Override
    public void blitTo(Framebuffer target){
        bindToRead();
        target.bindToWrite();  
        fb.blit(0, 0, getWidth(), getHeight(), 0, 0, target.getWidth(), target.getHeight(), GL_COLOR_BUFFER_BIT, GL_LINEAR);
    }
    
    @Override
    public void blitToWithDepth(Framebuffer target){
        bindToRead();
        target.bindToWrite();  
        fb.blit(0, 0, getWidth(), getHeight(), 0, 0, target.getWidth(), target.getHeight(), GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT, GL_NEAREST);
    }
    
    @Override
    public void blitToBack(){
        bindToRead();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glDrawBuffer(GL_BACK);
        fb.blit(0, 0, getWidth(), getHeight(), 0, 0, WindowController.getWidth(), WindowController.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }
    
    @Override
    public void refresh(){
        textures = new HashMap<>();
        renderbuffers = new ArrayList<>();
        usedAttachments = new ArrayList<>();
        lx = 0;
        ly = 0;
    }
    
    @Override
    public void enableRendering(){
        fb.bind(GL_FRAMEBUFFER);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glViewport(0, 0, lx, ly);
    }
    
    @Override
    public void restartRendering(){
        fb.bind(GL_FRAMEBUFFER);
        glViewport(0, 0, lx, ly);
    }
    
    @Override
    public void disableRendering(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, WindowController.getWindow().getWidth(), WindowController.getWindow().getHeight());
        
    }
    
    @Override
    public void checkForCompletion(){
        int comp;
        if((comp = fb.checkCompleteness()) != GL_FRAMEBUFFER_COMPLETE){
            GGConsole.error("OpenGLFramebuffer failed to complete, GL error " + comp);
        }
    }

    @Override
    public int getWidth(){
        return lx;
    }

    @Override
    public int getHeight(){
        return ly;
    }
}
