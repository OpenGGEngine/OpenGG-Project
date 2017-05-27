/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import com.opengg.core.engine.GGConsole;
import java.io.IOException;
import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage3D;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class ArrayTexture extends Texture{
    int layers = 0;
    
    ArrayTexture(String... paths) throws IOException{
        super();
        forceLoadTextures(paths);
    }
    
    public static ArrayTexture get(String... paths){
        try{
            ArrayTexture tex = new ArrayTexture(paths);
            return tex;
        }catch (Exception e){
            GGConsole.log("Failed to create texture array");
        }
        return null;
    }
    
    public void forceLoadTextures(String... paths) throws IOException{
        type = GL_TEXTURE_2D_ARRAY;
        
        TextureData[] datum = new TextureData[paths.length];
        long blength = 0;
        for(int i = 0; i < datum.length; i++){
            datum[i] = TextureBufferGenerator.getFastBuffer(paths[i]);
            blength += datum[i].buffer.limit();
        }
        
        width = datum[0].width;
        height = datum[0].height;
        layers = datum.length;
        
        ByteBuffer full = MemoryUtil.memAlloc((int) blength);
        
        for(TextureData data : datum){
            full.put((ByteBuffer)data.buffer);
        }
               
        full.flip();
        glActiveTexture(GL_TEXTURE0);
        this.texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_ARRAY,texture);
        glTexStorage3D(GL_TEXTURE_2D_ARRAY, 4, GL_RGBA8, width, height, layers);
        
        glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, 0, width, height, layers, GL_RGBA, GL_UNSIGNED_BYTE, full);
        
        glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_MAG_FILTER, GL_LINEAR );
        glTexParameterf(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_LOD_BIAS, -0.4f );
        glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D_ARRAY,GL_TEXTURE_WRAP_T,GL_REPEAT);
        glGenerateMipmap( GL_TEXTURE_2D_ARRAY );
        
    }
}
