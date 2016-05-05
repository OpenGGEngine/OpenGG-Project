/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.util.GlobalInfo;
import java.awt.Image;
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
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;


/**
 *
 * @author Javier
 */
public class Texture {
    private int fb;
    private int texture;
    private int depthbuffer;
    public static Texture blank;
    int width;
    int height;
    int rendsizex, rendsizey;
     
    public Texture(){
        //this.loc = loc;
    }
    
    public void useTexture(int loc){
        glActiveTexture(GL_TEXTURE0 + loc);
        glBindTexture(GL_TEXTURE_2D, texture);
    }
    
    public void useDepthTexture(int loc){
        glActiveTexture(GL_TEXTURE0 + loc);
        glBindTexture(GL_TEXTURE_2D, depthbuffer);       
    }
    
    ByteBuffer buffer;
    
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
    
    public void blitBuffer(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);   // Make sure no FBO is set as the draw framebuffer
          glBindFramebuffer(GL_READ_FRAMEBUFFER, fb); // Make sure your multisampled FBO is the read framebuffer
          glDrawBuffer(GL_BACK);                       // Set the back buffer as the draw buffer
          glBlitFramebuffer(0, 0, GlobalInfo.window.getWidth(), GlobalInfo.window.getHeight(), 0, 0, GlobalInfo.window.getWidth(), GlobalInfo.window.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }
    
    public int setupTexToBuffer(int sizex, int sizey){
        //glActiveTexture(GL_TEXTURE0 + loc);
        rendsizex = sizex;
        rendsizey = sizey;
        fb = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fb);
        glDrawBuffers(GL_COLOR_ATTACHMENT0);
        
        
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
        glFinish();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0,0,GlobalInfo.window.getWidth(),GlobalInfo.window.getHeight());
    }
    
    public int loadFromBuffer(ByteBuffer b, int fwidth, int fheight){
        //glActiveTexture(GL_TEXTURE0 + loc);
        
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        buffer = b;
        
        width = fwidth;
        height = fheight;
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, fwidth, fheight, 0, GL_RGBA, GL_UNSIGNED_BYTE, b);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        return texture;
    }
    
    public int loadTexture(String path, boolean flipped){
        glActiveTexture(GL_TEXTURE0);
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        

        try{
            
            BufferedImage image = new BufferedImage(1,1,1);
            if(path.substring(path.lastIndexOf(".")).equals(".tga")){
                loadtga(path);
            }else{
                InputStream in;
                ByteBuffer buffer;

                in = new FileInputStream(path);
                image = ImageIO.read(in);
            }
            AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
            transform.translate(0, -image.getHeight());
            AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            image = operation.filter(image, null);

            int width = image.getWidth();
            int height = image.getHeight();

            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
            buffer = BufferUtils.createByteBuffer(width * height * 4);
            for (int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++){
                //for (int x = width-1; x > 0; x--) {
                    /* Pixel as RGBA: 0xAARRGGBB */
                    int pixel;
                    if(flipped){
                        pixel = pixels[y * width + x];
                    }else{
                        pixel = pixels[height- y * width + x];
                    }
                    /* Red component 0xAARRGGBB >> (4 * 4) = 0x0000AARR */
                    buffer.put((byte) ((pixel >> 16) & 0xFF));

                    /* Green component 0xAARRGGBB >> (2 * 4) = 0x00AARRGG */
                    buffer.put((byte) ((pixel >> 8) & 0xFF));

                    /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
                    buffer.put((byte) (pixel & 0xFF));

                    /* Alpha component 0xAARRGGBB >> (6 * 4) = 0x000000AA */
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }

            buffer.flip();
            //buffer = TexBufferGen.genTex(path);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);//GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -2);
            glGenerateMipmap(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);
            
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(Texture.class.getName()).severe("File not found!");
            ex.printStackTrace();
        }  catch (Exception e){
            e.printStackTrace();
        }
        return texture;
    }
    public ByteBuffer getData(){
        return buffer;
    }
    
    private int offset;
    
    private BufferedImage loadtga(String filename) throws IOException{
        File f = new File(filename);
        byte[] buf = new byte[(int)f.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
        bis.read(buf);
        bis.close();
        return decode(buf);

    }
    private static int btoi(byte b)
        {
                int a = b;
                return (a<0?256+a:a);
        }

        private int read(byte[] buf)
        {
                return btoi(buf[offset++]);
        }

        public BufferedImage decode(byte[] buf) throws IOException
        {
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

}

