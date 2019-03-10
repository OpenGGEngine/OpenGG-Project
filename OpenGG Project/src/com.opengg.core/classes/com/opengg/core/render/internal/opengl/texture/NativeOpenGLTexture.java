/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.texture;

import com.opengg.core.render.RenderEngine;

import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

/**
 *
 * @author Javier
 */
public class NativeOpenGLTexture{
    private final int id;
    
    public NativeOpenGLTexture(){
        if(RenderEngine.validateInitialization()) id = -1;
        else id = glGenTextures();
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
    
    public void generateMipmap(int type){
        if(RenderEngine.validateInitialization()) return;
        glGenerateMipmap(type);
    }
    
    public int getID(){
        return id;
    }
    
    public void delete(){
        if(RenderEngine.validateInitialization()) return;
        glDeleteTextures(id);
    }
}
