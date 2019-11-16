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
    private String shader = "";
    private List<Tuple<Integer, Integer>> colorbinds = new ArrayList<>();
    private Framebuffer buffer;

    public Stage(String shader){
        this(shader, new Tuple<>(0,0));
    }

    public Stage(String shader, Tuple<Integer,Integer>... binds){
        this.shader = shader;
        colorbinds.addAll(List.of(binds));
        buffer = WindowFramebuffer.getFloatingPointWindowFramebuffer(binds.length);
    }

    public String getShader() {
        return shader;
    }

    public void render(){
        PostProcessController.currentBuffer = buffer;
        ShaderController.useConfiguration(shader);

        buffer.enableRendering();
        buffer.useEnabledAttachments();
        PostProcessController.renderable.render();
        buffer.disableRendering();
        for (Tuple<Integer, Integer> bind : colorbinds) {
            buffer.useTexture(bind.x, bind.y);
        }

    }
}
