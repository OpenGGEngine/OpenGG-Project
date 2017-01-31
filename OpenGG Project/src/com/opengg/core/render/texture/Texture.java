/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.engine.GGConsole;
import java.io.File;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL30.*;


/**
 *
 * @author Javier
 */
public class Texture {
    
    protected int texture;
    public static Texture blank;
    int width;
    int height;
    private int offset;
    //ByteBuffer buffer; 
    
    Texture(String path){
        forceLoadTexture(path, true);
    }
    
    Texture(){};
    
    public static Texture get(String path){
        Texture t;
        if((t = TextureManager.getTexture(path)) != null){
            return t;
        }else{
            if(new File(path).exists()){
                t = new Texture(path);
            }else{
                t =  TextureManager.getTexture("default");  
                GGConsole.warning("Could not find texture at " + path + ", using default instead");
            } 
            TextureManager.setTexture(path, t);
            return t;
        }
    }
    
    public void useTexture(int loc){
        glActiveTexture(GL_TEXTURE0 + loc);
        glBindTexture(GL_TEXTURE_2D, texture);
    }  
    
    public void setLODBias(int bias){
        glActiveTexture(GL_TEXTURE9);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, bias);
        glActiveTexture(GL_TEXTURE0);
    }
    
    public void setMagFilter(int filter){
        glActiveTexture(GL_TEXTURE9);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
        glActiveTexture(GL_TEXTURE0);
    }
    
    public void setMinFilter(int filter){
        glActiveTexture(GL_TEXTURE9);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
        glActiveTexture(GL_TEXTURE0);
    }
    
    public int loadFromBuffer(ByteBuffer b, int fwidth, int fheight){
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        width = fwidth;
        height = fheight;
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, fwidth, fheight, 0, GL_RGBA, GL_UNSIGNED_BYTE, b);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        return texture;
    } 
    
    public int forceLoadTexture(String path, boolean flipped){
        
        try{
            TextureData data = TextureBufferGenerator.getFastBuffer(path, flipped);
            
            width = data.width;
            height = data.height;
            ByteBuffer buffer = (ByteBuffer) data.buffer;
            
            glActiveTexture(GL_TEXTURE0);
            texture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);//GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -2);
            glGenerateMipmap(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);
            buffer.clear();
            return texture;
        } catch (Exception e){
            GGConsole.warning(path + " failed to load, using default texture instead");
            forceLoadTexture(TextureManager.defPath, false);
            TextureManager.setTexture(path, this);
        }
        return texture;
    }
    
    public ByteBuffer getData(){
        return null;
    }
    
    public void setAnisotropy(int level){
        if(GL.getCapabilities().GL_EXT_texture_filter_anisotropic){
            float lev = Math.min(level, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, lev);
        }
        
    }
 
    public void destroy(){
        
    }

}

