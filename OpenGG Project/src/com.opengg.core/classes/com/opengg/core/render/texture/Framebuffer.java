/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.WindowController;
import com.opengg.core.math.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glFlush;
import static org.lwjgl.opengl.GL11.glViewport;
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
public class Framebuffer {
    public static final int DEPTH = GL_DEPTH_ATTACHMENT;
    NativeGLFramebuffer fb;
    List<Integer> usedAttachments;
    HashMap<Integer, Texture> textures;
    List<NativeGLRenderbuffer> renderbuffers;
    int lx, ly;
    
    protected Framebuffer(){
        fb = new NativeGLFramebuffer();
        refresh();
    }
    
    public void bind(){
        fb.bind(GL_FRAMEBUFFER);
    }
    
    public void bindToRead(){
        fb.bind(GL_READ_FRAMEBUFFER);
    }
    
    public void bindToWrite(){
        fb.bind(GL_DRAW_FRAMEBUFFER);
    }
    
    public void useEnabledAttachments(){
        fb.bind(GL_FRAMEBUFFER);
       
        int[] attachments = new int[usedAttachments.size()];
        for(int i = 0; i < attachments.length; i++){
            if(usedAttachments.get(i) == GL_DEPTH_ATTACHMENT) continue;
            attachments[i] = usedAttachments.get(i);
        }

        glDrawBuffers(attachments);
    }
    
    public void useTexture(int attachment, int loc){
        if(attachment != DEPTH) attachment = GL_COLOR_ATTACHMENT0 + attachment;
        Texture tex = textures.get(attachment);
        if(tex != null){
            tex.use(loc);
        }
    }
    
    public void attachColorTexture(int width, int height, int attachment){
        attachTexture(width, height, GL_RGBA, GL_RGBA8, GL_UNSIGNED_BYTE,  GL_COLOR_ATTACHMENT0 + attachment);
    }
    
    public void attachFloatingPointTexture(int width, int height, int attachment){
        attachTexture(width, height, GL_RGBA, GL_RGBA16F, GL_FLOAT, GL_COLOR_ATTACHMENT0 + attachment);
    }
    
    public void attachDepthStencilTexture(int width, int height){
        attachTexture(width, height, GL_DEPTH_STENCIL, GL_DEPTH24_STENCIL8, GL_UNSIGNED_INT_24_8, GL_DEPTH_ATTACHMENT);
    }
    
    public void attachDepthRenderbuffer(int width, int height){
        attachRenderbuffer(width, height, GL_DEPTH_COMPONENT, GL_DEPTH_ATTACHMENT);
    }
    
    public void attachRenderbuffer(int width, int height, int storage, int attachment){
        NativeGLRenderbuffer rb = new NativeGLRenderbuffer();
        rb.bind();
        rb.createStorage(storage, width, height);
        
        fb.bind(GL_FRAMEBUFFER);
        fb.attachRenderbuffer(attachment, rb.id);
        checkForCompletion();
        
        renderbuffers.add(rb);
        usedAttachments.add(attachment);
        
        if(lx < width) lx = width;
        if(ly < height) ly = height;
    }
    
    private void attachTexture(int width, int height, int format, int intformat, int input, int attachment){ 
        Texture tex = Texture.get2DFramebufferTexture(width, height, format, intformat, input);
        
        fb.bind(GL_FRAMEBUFFER);
        fb.attachTexture(attachment, tex.getID(), 0);
        checkForCompletion();

        textures.put(attachment, tex);
        usedAttachments.add(attachment);

        if(lx < width) lx = width;
        if(ly < height) ly = height;
    }
    
    public void blitTo(Framebuffer target){
        bindToRead();
        target.bindToWrite();  
        fb.blit(0, 0, lx, ly, 0, 0, target.lx, target.ly, GL_COLOR_BUFFER_BIT, GL_LINEAR);
    }
    
    public void blitToWithDepth(Framebuffer target){
        bindToRead();
        target.bindToWrite();  
        fb.blit(0, 0, lx, ly, 0, 0, target.lx, target.ly, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT, GL_NEAREST);
    }
    
    public void blitToBack(){
        bindToRead();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glDrawBuffer(GL_BACK);
        fb.blit(0, 0, lx, ly, 0, 0, WindowController.getWidth(), WindowController.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }
    
    public void refresh(){
        textures = new HashMap<>();
        renderbuffers = new ArrayList<>();
        usedAttachments = new ArrayList<>();
        lx = 0;
        ly = 0;
    }
    
    public void enableRendering(){
        glBindTexture(GL_TEXTURE_2D, 0);
        fb.bind(GL_FRAMEBUFFER);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glViewport(0, 0, lx, ly);
    }
    
    public void disableRendering(){
        glFlush();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, WindowController.getWindow().getWidth(), WindowController.getWindow().getHeight());
        
    }
    
    public void checkForCompletion(){
        int comp;
        if((comp = fb.checkCompleteness()) != GL_FRAMEBUFFER_COMPLETE){
            GGConsole.error("Framebuffer failed to complete, GL error " + comp);
        }
    }
    
    public static Framebuffer generateFramebuffer(){
        Framebuffer nframe = new Framebuffer();
        return nframe;
    }
}
