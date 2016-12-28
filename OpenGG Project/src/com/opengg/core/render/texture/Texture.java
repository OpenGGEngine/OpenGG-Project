/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.engine.GGConsole;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;


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
            t = new Texture(path);
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
        ByteBuffer buffer;
        glActiveTexture(GL_TEXTURE0);
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        try{
            TextureData data = TextureBufferGenerator.getFastBuffer(path, flipped);
            width = data.width;
            height = data.height;
            buffer = (ByteBuffer) data.buffer;
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);//GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -2);
            glGenerateMipmap(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);
            buffer.clear();
        } catch (Exception e){
            GGConsole.warning(path + " failed to load: ");
            e.printStackTrace();
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
    private BufferedImage loadtga(String filename) throws IOException{
        File f = new File(filename);
        byte[] buf = new byte[(int)f.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        bis.read(buf);
        bis.close();
        return decode(buf);

    }
    private static int btoi(byte b){
        int a = b;
        return (a<0?256+a:a);
    }
    private int read(byte[] buf){
        return btoi(buf[offset++]);
    }
    public BufferedImage decode(byte[] buf) throws IOException{
        offset = 0;

        // Reading header
        for (int i=0;i<12;i++)
            read(buf);
        int width = read(buf)+(read(buf)<<8);
        int height = read(buf)+(read(buf)<<8);
        read(buf);
        read(buf);

        // Reading data
        int n = width*height;
        int[] pixels = new int[n];
        int idx=0;

        while (n>0)
        {
            int nb = read(buf);
            if ((nb&0x80)==0)
            {
                for (int i=0;i<=nb;i++)
                {
                    int b = read(buf);
                    int g = read(buf);
                    int r = read(buf);
                    pixels[idx++] = 0xff000000 | (r<<16) | (g<<8) | b;
                }
            }
            else
            {
                nb &= 0x7f;
                int b = read(buf);
                int g = read(buf);
                int r = read(buf);
                int v = 0xff000000 | (r<<16) | (g<<8) | b;
                for (int i=0;i<=nb;i++)
                    pixels[idx++] = v;
            }
            n-=nb+1;
        }

        BufferedImage bimg = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        bimg.setRGB(0,0,width,height,pixels,0,width);
        return bimg;
    }
    
    public void destroy(){
        
    }

}

