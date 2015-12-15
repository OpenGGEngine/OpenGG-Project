/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
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
    
    public void use(){
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
        glActiveTexture(GL_TEXTURE0);
    }
    
    public int loadTexture(String path){
 
        InputStream in;
        
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
                in = new FileInputStream(path + endings[i]);
                BufferedImage image = ImageIO.read(in);

                AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
                transform.translate(0, -image.getHeight());
                AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                image = operation.filter(image, null);

                width = image.getWidth();
                height = image.getHeight();
                
                buffer[i] = BufferUtils.createByteBuffer(width * height * 4);
                int[] pixels = new int[width * height];
                image.getRGB(0, 0, width, height, pixels, 0, width);
                for (int y = height-1; y > 0; y--) {
                    for(int x = 0; x < width; x++){
                    //for (int x = width-1; x > 0; x--) {
                        /* Pixel as RGBA: 0xAARRGGBB */
                        int pixel = pixels[y * width + x];

                        /* Red component 0xAARRGGBB >> (4 * 4) = 0x0000AARR */
                        buffer[i].put((byte) ((pixel >> 16) & 0xFF));

                        /* Green component 0xAARRGGBB >> (2 * 4) = 0x00AARRGG */
                        buffer[i].put((byte) ((pixel >> 8) & 0xFF));

                        /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
                        buffer[i].put((byte) (pixel & 0xFF));

                        /* Alpha component 0xAARRGGBB >> (6 * 4) = 0x000000AA */
                        buffer[i].put((byte) ((pixel >> 24) & 0xFF));
                    }
                }

                buffer[i].flip();
            }
              
            texture = glGenTextures();

            glBindTexture(GL_TEXTURE_CUBE_MAP, texture);	// Make it a cubemap
            
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
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Texture.class.getName()).severe("File not found!");
        }  catch (Exception e){
            e.printStackTrace();
        }
        return texture;
    }
}
