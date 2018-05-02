/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.render.window.WindowController;
import com.opengg.core.math.Vector2i;
import com.opengg.core.render.window.WindowResizeListener;

/**
 *
 * @author Javier
 */
public class WindowFramebuffer implements WindowResizeListener, Framebuffer{
    int targets;
    boolean fp;

    private Framebuffer underlyingBuffer;
    
    protected WindowFramebuffer(){}
    
    public static WindowFramebuffer getWindowFramebuffer(int targets){
        return getWindowFramebuffer(targets, false);
    }
    
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
        refresh();
        Vector2i size = new Vector2i(WindowController.getWidth(),
                WindowController.getHeight());
        
        bind();
        for(int i = 0; i < targets; i++){
            if(fp)
                attachFloatingPointTexture(size.x, size.y, i);
            else
                attachColorTexture(size.x, size.y, i);
        }
        attachDepthTexture(size.x, size.y);
        checkForCompletion();
    }

    @Override
    public void onResize(Vector2i size) {
        regen();
    }


    @Override
    public void bind(){
        underlyingBuffer.bind();
    }

    @Override
    public void bindToRead(){
        underlyingBuffer.bindToRead();
    }

    @Override
    public void bindToWrite(){
        underlyingBuffer.bindToWrite();
    }

    @Override
    public void useEnabledAttachments(){
        underlyingBuffer.useEnabledAttachments();
    }

    @Override
    public void useTexture(int attachment, int loc){
        underlyingBuffer.useTexture(attachment, loc);
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
    public void attachTexture(int width, int height, int format, int intformat, int input, int attachment){
        underlyingBuffer.attachTexture(width, height, format, intformat, input, attachment);
    }

    @Override
    public void blitTo(Framebuffer target){
        underlyingBuffer.blitTo(target);
    }

    @Override
    public void blitToWithDepth(Framebuffer target){
        underlyingBuffer.blitToWithDepth(target);
    }

    @Override
    public void blitToBack(){
        underlyingBuffer.blitToBack();
    }

    @Override
    public void refresh(){
        underlyingBuffer.refresh();
    }

    @Override
    public void enableRendering(){
        underlyingBuffer.enableRendering();
    }

    @Override
    public void restartRendering(){
        underlyingBuffer.restartRendering();
    }

    @Override
    public void disableRendering(){
        underlyingBuffer.disableRendering();
    }

    @Override
    public void checkForCompletion(){
        underlyingBuffer.checkForCompletion();
    }

    @Override
    public int getWidth(){
        return 0;
    }

    @Override
    public int getHeight(){
        return 0;
    }
}
