/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.render.texture.Framebuffer;

/**
 *
 * @author Javier
 */
public class Stage {
    String shader;
    Framebuffer buffer;
    int order;
    
    public Stage(String shader, int order){
        buffer = Framebuffer.getFramebuffer(OpenGG.window.getWidth(), OpenGG.window.getHeight());
        this.shader = shader;
        this.order = order;
    }
}
