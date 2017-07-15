/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.WindowFramebuffer;

/**
 *
 * @author Javier
 */
public class Stage {
    String shader;
    Framebuffer buffer;
    
    public Stage(String shader){
        buffer = WindowFramebuffer.getWindowFramebuffer(1);
        this.shader = shader;
    }
    
    public void use(){
        PostProcessPipeline.current = this;
        ShaderController.useConfiguration(shader);
        buffer.enableRendering();
        buffer.useEnabledAttachments();
    }
    
    public void finalizeAtLoc(int end){
        buffer.disableRendering();
        buffer.useTexture(end, 0);     
    }
}
