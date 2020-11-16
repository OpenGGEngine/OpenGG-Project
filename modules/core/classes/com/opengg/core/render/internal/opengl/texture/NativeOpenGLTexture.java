/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.texture;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.system.NativeResource;
import com.opengg.core.system.NativeResourceManager;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL42.glTexStorage3D;
import static org.lwjgl.opengl.GL43.glTextureView;
import static org.lwjgl.opengl.GL45.*;

/**
 *
 * @author Javier
 */
public class NativeOpenGLTexture implements NativeResource{
    private final int id;
    
    public NativeOpenGLTexture(int target){
        if(RenderEngine.validateInitialization()) id = -1;
        else id = glCreateTextures(target);
        NativeResourceManager.registerNativeResource(this);
    }

    public NativeOpenGLTexture(){
        if(RenderEngine.validateInitialization()) id = -1;
        else id = glGenTextures();
        NativeResourceManager.registerNativeResource(this);
    }

    public void createView(int target, int original, int internalFormat,
                           int minLevels, int levelCount, int minLayer, int numLayers){
        glTextureView(id, target, original, internalFormat, minLevels, levelCount, minLayer, numLayers);
    }

    public void bindToUnit(int loc){
        if(RenderEngine.validateInitialization()) return;
        glBindTextureUnit(loc, id);
    }
    
    public void set2DImageStorage(int levels, int format, int width, int height){
        if(RenderEngine.validateInitialization()) return;
        glTextureStorage2D(id, levels, format, width, height);
    }
    
    public void set3DImageStorage(int levels, int format, int width, int height, int depth){
        if(RenderEngine.validateInitialization()) return;
        glTextureStorage3D(id, levels, format, width, height, depth);
    }
    
    public void set2DImageData(int level, int width, int height, int format, int type, ByteBuffer data){
        if(RenderEngine.validateInitialization()) return;
        glTextureSubImage2D(id, level, 0, 0, width, height, format, type, data);
    }

    public void setImageDataCompressed(int level, int width, int height, int format, ByteBuffer data){
        if(RenderEngine.validateInitialization()) return;
        glCompressedTextureSubImage2D(id,level,0,0, width,height,format,data);
    }
    
    public void set3DImageData(int level, int width, int height, int depth, int format, int type, ByteBuffer data){
        if(RenderEngine.validateInitialization()) return;
        glTextureSubImage3D(id, level, 0,0,0, width, height, depth, format, type, data);
    }
    
    public void set2DSubImageData(int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer data){
        if(RenderEngine.validateInitialization()) return;
        glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format, type, data);
    }
    
    public void set3DSubImageData(int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ByteBuffer data){
        if(RenderEngine.validateInitialization()) return;
        glTextureSubImage3D(id, level, xoffset, yoffset, zoffset, width, height, depth, format, type, data);
    }
    
    public void setParameteri(int param, int value){
        if(RenderEngine.validateInitialization()) return;
        glTextureParameteri(id, param, value);
    }
    
    public void setParameterf(int param, float value){
        if(RenderEngine.validateInitialization()) return;
        glTextureParameterf(id, param, value);
    }

    public void setParameterfv(int param, float[] value){
        if(RenderEngine.validateInitialization()) return;
        glTextureParameterfv(id, param, value);
    }
    
    public void generateMipmap(){
        if(RenderEngine.validateInitialization()) return;
        glGenerateTextureMipmap(id);
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
