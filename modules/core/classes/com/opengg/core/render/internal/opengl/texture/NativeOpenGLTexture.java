/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.texture;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.system.NativeResource;
import com.opengg.core.system.NativeResourceManager;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL13.glCompressedTexImage2D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

/**
 *
 * @author Javier
 */
public class NativeOpenGLTexture implements NativeResource{
    private final int id;
    
    public NativeOpenGLTexture(){
        if(RenderEngine.validateInitialization()) id = -1;
        else id = glGenTextures();
        NativeResourceManager.registerNativeResource(this);
    }
    
    public void bind(int type){
        if(RenderEngine.validateInitialization()) return;
        glBindTexture(type, id);
    }
    
    public void setActiveTexture(int loc){
        if(RenderEngine.validateInitialization()) return;
        glActiveTexture(loc);
    }
    
    public void setImageStorage(int target, int levels, int format, int width, int height){
        if(RenderEngine.validateInitialization()) return;
        glTexStorage2D(target, levels, format, width, height);
    }
    
    public void setImageStorage(int target, int levels, int format, int width, int height, int depth){
        if(RenderEngine.validateInitialization()) return;
        glTexStorage3D(target, levels, format, width, height, depth);
    }
    
    public void setImageData(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer data){
        if(RenderEngine.validateInitialization()) return;
        glTexImage2D(target, level, internalformat, width, height, border, format, type, data);
    }

    public void setImageDataCompressed(int target, int level, int internalformat, int width, int height, int border, ByteBuffer data){
        if(RenderEngine.validateInitialization()) return;
        glCompressedTexImage2D(target,level,internalformat,width,height,border,data);
    }
    
    public void setImageData(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ByteBuffer data){
        if(RenderEngine.validateInitialization()) return;
        glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, data);
    }
    
    public void setSubImageData(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer data){
        if(RenderEngine.validateInitialization()) return;
        glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, data);
    }
    
    public void setSubImageData(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ByteBuffer data){
        if(RenderEngine.validateInitialization()) return;
        glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, data);
    }
    
    public void setParameteri(int type, int param, int value){
        if(RenderEngine.validateInitialization()) return;
        glTexParameteri(type, param, value);
    }
    
    public void setParameterf(int type, int param, float value){
        if(RenderEngine.validateInitialization()) return;
        glTexParameterf(type, param, value);
    }

    public void setParameterfv(int type, int param, float[] value){
        if(RenderEngine.validateInitialization()) return;
        glTexParameterfv(type, param, value);
    }
    
    public void generateMipmap(int type){
        if(RenderEngine.validateInitialization()) return;
        glGenerateMipmap(type);
    }
    
    public int getID(){
        return id;
    }

    public void destroy(){
        if(RenderEngine.validateInitialization()) return;
        glDeleteTextures(id);
    }

    @Override
    public Runnable onDestroy(){
        int id2 = id;
        return () -> glDeleteTextures(id2);
    }
}
