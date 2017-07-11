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
    int colorcount;
    
    protected WindowFramebuffer(){}
    
    public static WindowFramebuffer getWindowFramebuffer(int targets){
        WindowFramebuffer wfb = new WindowFramebuffer();
        wfb.regen(targets);
        wfb.colorcount = targets;
        WindowController.addResizeListener(wfb);
        return wfb;
    }
    
    private void regen(int targets){
        this.fb = new NativeGLFramebuffer();
        refresh();
        Vector2i size = new Vector2i(WindowController.getWindow().getWidth(),
                WindowController.getWindow().getHeight());
        bind();
        for(int i = 0; i < targets; i++){
            attachColorTexture(size.x, size.y, i);
        }
        attachDepthStencilTexture(size.x, size.y);
        checkForCompletion();

    }

    @Override
    public void onResize(Vector2i size) {
        regen(colorcount);
    }
}
