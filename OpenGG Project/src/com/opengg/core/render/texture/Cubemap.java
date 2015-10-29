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
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
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
    public int loadTexture(String path){
 
        InputStream in;
        
        try {
            
            glActiveTexture(GL_TEXTURE1);
            in = new FileInputStream(path);
            BufferedImage image = ImageIO.read(in);
            
            AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
            transform.translate(0, -image.getHeight());
            AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            image = operation.filter(image, null);
            
            width = image.getWidth();
            height = image.getHeight();

            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
            for(int i = 0; i < buffer.length; i++){
                buffer[i] = BufferUtils.createByteBuffer(width * height * 4);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
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

            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL_RGBA, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer[0]); // postive x
            glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL_RGBA, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer[1]); // negative x
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL_RGBA, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer[2]); // postive y
            glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL_RGBA, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer[3]); // negative y
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL_RGBA, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer[4]); // positive z
            glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL_RGBA, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer[5]); // negative z

            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);	// Set far filtering mode
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            
            glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
            
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Texture.class.getName()).severe("File not found!");
        }  catch (Exception e){
            e.printStackTrace();
        }
        return texture;
    }
}
