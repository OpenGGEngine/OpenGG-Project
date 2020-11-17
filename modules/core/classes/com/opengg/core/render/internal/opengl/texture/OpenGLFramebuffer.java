/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.texture;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Vector3i;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.OpenGLRenderer;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.Renderbuffer;
import com.opengg.core.render.texture.Texture;

import java.util.*;

import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author Javier
 */
public class OpenGLFramebuffer implements Framebuffer{
    NativeOpenGLFramebuffer fb;
    List<Integer> enabledFragmentAttachments;
    Map<Integer, Texture> textures;
    List<Renderbuffer> renderbuffers;
    int lx, ly;
    
    public OpenGLFramebuffer(){
        fb = new NativeOpenGLFramebuffer();
        enabledFragmentAttachments = new ArrayList<>();
        textures = new HashMap<>();
        renderbuffers = new ArrayList<>();
    }

    public Texture getTexture(int attachment){
        Texture tex;
        if(attachment == -1)
            tex = textures.get(GL_DEPTH_ATTACHMENT);
        else
            tex = textures.get(GL_COLOR_ATTACHMENT0 + attachment);
        return tex;
    }

    @Override
    public List<Texture> getTextures(){
        return List.copyOf(textures.values());
    }

    @Override
    public void attachColorTexture(int width, int height, int attachment){
        attachTexture(Texture.TextureType.TEXTURE_2D, width, height, Texture.SamplerFormat.RGBA, Texture.TextureFormat.RGBA8, Texture.InputFormat.UNSIGNED_BYTE,  GL_COLOR_ATTACHMENT0 + attachment);
    }
    
    @Override
    public void attachFloatingPointTexture(int width, int height, int attachment){
        attachTexture(Texture.TextureType.TEXTURE_2D, width, height, Texture.SamplerFormat.RGBA, Texture.TextureFormat.RGBA16F, Texture.InputFormat.FLOAT, GL_COLOR_ATTACHMENT0 + attachment);
    }
    
    @Override
    public void attachDepthStencilTexture(int width, int height){
        attachTexture(Texture.TextureType.TEXTURE_2D, width, height, Texture.SamplerFormat.DEPTH_STENCIL, Texture.TextureFormat.DEPTH24_STENCIL8, Texture.InputFormat.UNSIGNED_INT_24_8, GL_DEPTH_ATTACHMENT);
    }
    
    @Override
    public void attachDepthTexture(int width, int height){
        attachTexture(Texture.TextureType.TEXTURE_2D, width, height, Texture.SamplerFormat.DEPTH, Texture.TextureFormat.DEPTH32, Texture.InputFormat.FLOAT, GL_DEPTH_ATTACHMENT);
    }

    @Override
    public void attachDepthCubemap(int width, int height){
        attachTexture(Texture.TextureType.TEXTURE_CUBEMAP, width, height, Texture.SamplerFormat.DEPTH, Texture.TextureFormat.DEPTH32, Texture.InputFormat.FLOAT, GL_DEPTH_ATTACHMENT);
    }

    @Override
    public void attachColorCubemap(int width, int height, int attachment){
        attachTexture(Texture.TextureType.TEXTURE_CUBEMAP, width, height, Texture.SamplerFormat.RGBA, Texture.TextureFormat.RGBA8, Texture.InputFormat.UNSIGNED_BYTE, GL_COLOR_ATTACHMENT0 + attachment);
    }

    public void attachTexture(Texture.TextureType type, int width, int height, Texture.SamplerFormat format, Texture.TextureFormat internalFormat, Texture.InputFormat input, int attachment){
        var texture = !type.equals(Texture.TextureType.TEXTURE_CUBEMAP) ?
                new OpenGLTexture(Texture.config()
                    .samplerFormat(format)
                    .internalFormat(internalFormat)
                    .inputFormat(input)
                    .wrapType(Texture.WrapType.REPEAT)
                    .minimumFilter(Texture.FilterType.LINEAR)
                    .maxFilter(Texture.FilterType.LINEAR),
                    new Texture.TextureStorageProperties(new Vector3i(width, height,1), 1, 1)) :
                new OpenGLTexture(Texture.cubemapConfig().
                        samplerFormat(format).
                        internalFormat(internalFormat).
                        inputFormat(input)
                        .wrapType(Texture.WrapType.CLAMP_BORDER)
                        .minimumFilter(Texture.FilterType.LINEAR)
                        .maxFilter(Texture.FilterType.NEAREST),
                        new Texture.TextureStorageProperties(new Vector3i(width, height,1), 1, 1));

        fb.attachTexture(attachment, (int) texture.getID(), 0);
        checkForCompletion();

        textures.put(attachment, texture);
        enabledFragmentAttachments.add(attachment);

        if(lx < width) lx = width;
        if(ly < height) ly = height;
    }

    @Override
    public void attachDepthRenderbuffer(int width, int height){
        attachRenderbuffer(width, height, GL_DEPTH_COMPONENT, GL_DEPTH_ATTACHMENT);
    }
    
    @Override
    public void attachRenderbuffer(int width, int height, int storage, int attachment){
        Renderbuffer rb = Renderbuffer.create(width, height, storage);
        fb.attachRenderbuffer(attachment, rb.getID());
        checkForCompletion();
        
        renderbuffers.add(rb);
        enabledFragmentAttachments.add(attachment);

        if(lx < width) lx = width;
        if(ly < height) ly = height;
    }

    @Override
    public void blitTo(Framebuffer target){
        var glTarget = (OpenGLFramebuffer) target;
        fb.blit(glTarget.fb.getId(), 0, 0, getWidth(), getHeight(), 0, 0, target.getWidth(), target.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }
    
    @Override
    public void blitToBack(){
        fb.blit(0,0, 0, getWidth(), getHeight(), 0, 0, WindowController.getWidth(), WindowController.getHeight(), GL_COLOR_BUFFER_BIT , GL_NEAREST);
    }


    @Override
    public void clearFramebuffer() {
        fb.bind(GL_FRAMEBUFFER);
        fb.clear();
    }

    @Override
    public void enableRendering(int x1, int y1, int x2, int y2) {
        ((OpenGLRenderer) RenderEngine.renderer).setCurrentFramebuffer(this);
        fb.setDrawBuffers(enabledFragmentAttachments.stream().mapToInt(i -> i).filter(i -> i != GL_DEPTH_ATTACHMENT).toArray());
        fb.bind(GL_FRAMEBUFFER);
        glViewport(x1, y1, x2, y2);
    }
    
    @Override
    public void disableRendering(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, WindowController.getWindow().getWidth(), WindowController.getWindow().getHeight());
    }
    
    public void checkForCompletion(){
        int comp;
        if((comp = fb.checkCompleteness()) != GL_FRAMEBUFFER_COMPLETE){
            GGConsole.error("OpenGLFramebuffer failed to complete, GL error " + comp);
        }
    }

    @Override
    public int getWidth(){
        return lx;
    }

    @Override
    public int getHeight(){
        return ly;
    }
}
