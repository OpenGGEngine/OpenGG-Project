/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.engine.WindowController;
import com.opengg.core.math.Vector2i;
import com.opengg.core.render.window.WindowResizeListener;

/**
 *
 * @author Javier
 */
public class WindowFramebuffer extends Framebuffer implements WindowResizeListener{
    int targets;
    boolean fp;
    
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
        this.fb = new NativeGLFramebuffer();
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
}
