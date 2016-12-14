/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import static org.lwjgl.opengl.GL13.*;

/**
 *
 * @author Javier
 */
public class Cubemap {
    int texture;
    int width;
    int height;
    ByteBuffer[] buffer = new ByteBuffer[6];
    
    public void use(int loc){
        glActiveTexture(GL_TEXTURE0 + loc);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
    }
    
    public int loadTexture(String path){
        
        String[] endings = new String[6];
        endings[0] = "_ft.png";
        endings[1] = "_bk.png";
        endings[2] = "_up.png";
        endings[3] = "_dn.png";
        endings[4] = "_rt.png";
        endings[5] = "_lf.png";
        
        try {
            
            for(int i = 0; i < buffer.length; i++){
                glActiveTexture(GL_TEXTURE2);
                TextureData info = TextureBufferGenerator.getFastBuffer(path + endings[i]);
                buffer[i] = (ByteBuffer) info.buffer;
                width = info.width;
                height = info.height;
            }
              
            texture = glGenTextures();

            glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
            
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer[0]); // postive x
            glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer[1]); // negative x
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer[2]); // postive y
            glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer[3]); // negative y
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer[4]); // positive z
            glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer[5]); // negative z

            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);	// Set far filtering mode
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE); 
            //glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            //glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
         
            glActiveTexture(GL_TEXTURE0);

        }  catch (Exception e){
            Logger.getLogger(Texture.class.getName()).log(Level.WARNING, "Could not load texture at {0}", path);
            e.printStackTrace();
        }
        return texture;
    }
}
