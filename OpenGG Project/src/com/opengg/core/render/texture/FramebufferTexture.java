/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import com.opengg.core.engine.EngineInfo;
import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 *
 * @author Javier
 */
public class FramebufferTexture extends Texture {
    protected int fb;
    protected int depthbuffer;
    protected int texture2;
    protected int stencil;
    
    int rendsizex, rendsizey;
    
    public void drawColorAttachment(){
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
    }
    
    public void useDepthTexture(int loc){
        glActiveTexture(GL_TEXTURE0 + loc);
        glBindTexture(GL_TEXTURE_2D, depthbuffer);       
    }
        
    public void blitBuffer(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);   // Make sure no FBO is set as the draw framebuffer
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fb); // Make sure your multisampled FBO is the read framebuffer
        glDrawBuffer(GL_BACK);                       // Set the back buffer as the draw buffer
        glBlitFramebuffer(0, 0, EngineInfo.window.getWidth(), EngineInfo.window.getHeight(), 0, 0, EngineInfo.window.getWidth(), EngineInfo.window.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }
    
    public static FramebufferTexture getFramebuffer(int sizex, int sizey){
        FramebufferTexture t = new FramebufferTexture();
        t.setupTexToBuffer(sizex, sizey);
        return t;
    }
    
    public int setupTexToBuffer(int sizex, int sizey){
        //glActiveTexture(GL_TEXTURE0 + loc);
        rendsizex = sizex;
        rendsizey = sizey;
        fb = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fb);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        
        
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0,GL_RGB, sizex, sizey, 0,GL_RGB, 
                GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, texture, 0);
        
        depthbuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthbuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, sizex, sizey, 0, GL_DEPTH_COMPONENT, 
                GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthbuffer, 0);
        
        try{
            if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
                throw new Exception("Buffer failed to generate!");
                
            }
        }catch(Exception e){
            e.printStackTrace();
        } 
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);    
        return texture;
    }
    
    public void startTexRender(){
        //glActiveTexture(GL_TEXTURE0 + loc);
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, fb);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0,0,rendsizex, rendsizey); 
    }
    
    public void endTexRender(){
        glFlush();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0,0,EngineInfo.window.getWidth(),EngineInfo.window.getHeight());
    }
}
