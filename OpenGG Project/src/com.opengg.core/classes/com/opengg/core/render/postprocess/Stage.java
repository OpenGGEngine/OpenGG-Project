/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.math.Tuple;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.WindowFramebuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Stage {
    String shader = "";
    List<Tuple<Integer, Integer>> colorbinds = new ArrayList<>();
    Framebuffer buffer;
    
    public Stage(String shader){
        this(shader, new Tuple<>(0,1));
    }
    
    public Stage(String shader, Tuple<Integer,Integer>... binds){
        this.shader = shader;
        colorbinds.addAll(List.of(binds));
        buffer = WindowFramebuffer.getWindowFramebuffer(binds.length);
    }
    
    public void render(){
        PostProcessController.currentBuffer = buffer;
        ShaderController.useConfiguration(shader);     
        buffer.enableRendering();
        buffer.useEnabledAttachments();
        PostProcessController.drawable.render();
        buffer.disableRendering();
        for(Tuple<Integer,Integer> bind : colorbinds){
            buffer.useTexture(bind.x, bind.y);
        }
    }
}
