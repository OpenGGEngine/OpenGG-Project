/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Framebuffer;

/**
 *
 * @author Javier
 */
public class Stage {
    String shader;
    Framebuffer buffer;
    
    public Stage(String shader){
        buffer = Framebuffer.getFramebuffer(OpenGG.window.getWidth(), OpenGG.window.getHeight());
        this.shader = shader;
    }
    
    public void use(){
        PostProcessPipeline.current = this;
        ShaderController.useConfiguration(shader);
        buffer.enableColorAttachments();
        buffer.startTexRender();
    }
    
    public void finalizeAtLoc(int end){
        buffer.endTexRender();
        buffer.useTexture(end);
    }
}
