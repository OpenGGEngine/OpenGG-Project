/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.render.window.WindowController;
import com.opengg.core.math.Vector2i;
import com.opengg.core.render.window.WindowResizeListener;

import java.util.List;

/**
 * Represents a {@link Framebuffer} that automatically resizes when the window size is changed
 * @author Javier
 */
public class WindowFramebuffer implements WindowResizeListener, Framebuffer{
    int targets;
    boolean fp;

    private Framebuffer underlyingBuffer;
    
    protected WindowFramebuffer(){}

    /**
     * Create a window framebuffer with {@code targets} color targets in byte format and a depth buffer
     * @param targets Amount of targets
     * @return
     */
    public static WindowFramebuffer getWindowFramebuffer(int targets){
        return getWindowFramebuffer(targets, false);
    }

    /**
     * Create a window framebuffer with {@code targets} color targets in floating point format and a depth buffer
     * @param targets Amount of targets
     * @return
     */
    public static WindowFramebuffer getFloatingPointWindowFramebuffer(int targets){
        return getWindowFramebuffer(targets, true);
    }
    
    private static WindowFramebuffer getWindowFramebuffer(int targets, boolean fp){
        WindowFramebuffer wfb = new WindowFramebuffer();  
        wfb.targets = targets;
        wfb.fp = fp;
        wfb.regen();
        WindowController.addResizeListener(wfb);
        return wfb;
    }
    
    private void regen(){
        this.underlyingBuffer = Framebuffer.generateFramebuffer();
        Vector2i size = new Vector2i(WindowController.getWidth(),
                WindowController.getHeight());

        for(int i = 0; i < targets; i++){
            if(fp)
                attachFloatingPointTexture(size.x, size.y, i);
            else
                attachColorTexture(size.x, size.y, i);
        }
        attachDepthTexture(size.x, size.y);
    }

    @Override
    public void onResize(Vector2i size) {
        regen();
    }

    @Override
    public Texture getTexture(int attachment){
        return underlyingBuffer.getTexture(attachment);
    }

    @Override
    public List<Texture> getTextures(){
        return underlyingBuffer.getTextures();
    }

    @Override
    public void attachColorTexture(int width, int height, int attachment){
        underlyingBuffer.attachColorTexture(width, height, attachment);
    }

    @Override
    public void attachFloatingPointTexture(int width, int height, int attachment){
        underlyingBuffer.attachFloatingPointTexture(width, height, attachment);
    }

    @Override
    public void attachDepthStencilTexture(int width, int height){
        underlyingBuffer.attachDepthStencilTexture(width, height);
    }

    @Override
    public void attachDepthCubemap(int width, int height){
        underlyingBuffer.attachDepthCubemap(width, height);
    }

    @Override
    public void attachDepthTexture(int width, int height){
        underlyingBuffer.attachDepthTexture(width, height);
    }

    @Override
    public void attachDepthRenderbuffer(int width, int height){
        underlyingBuffer.attachDepthRenderbuffer(width, height);
    }

    @Override
    public void attachRenderbuffer(int width, int height, int storage, int attachment){
        underlyingBuffer.attachRenderbuffer(width, height, storage, attachment);
    }

    @Override
    public void attachColorCubemap(int width, int height, int attachment) {

    }

    @Override
    public void blitTo(Framebuffer target){
        underlyingBuffer.blitTo(target);
    }

    @Override
    public void blitToBack(){
        underlyingBuffer.blitToBack();
    }

    @Override
    public void clearFramebuffer() {
        underlyingBuffer.clearFramebuffer();
    }

    @Override
    public void enableRendering(int x1, int y1, int x2, int y2) {
        underlyingBuffer.enableRendering(x1,y1,x2,y2);
    }

    @Override
    public void disableRendering(){
        underlyingBuffer.disableRendering();
    }

    @Override
    public int getWidth(){
        return underlyingBuffer.getWidth();
    }

    @Override
    public int getHeight(){
        return underlyingBuffer.getHeight();
    }
}
