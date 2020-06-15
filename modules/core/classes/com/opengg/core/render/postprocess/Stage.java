/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.math.util.Tuple;
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
    private final List<Tuple<Integer, String>> colorbinds = new ArrayList<>();
    private final Framebuffer stageBuffer;

    public Stage(String shader){
        this(shader, Tuple.of(0,"Kd"));
    }

    public Stage(String shader, Tuple<Integer,String>... binds){
        this.shader = shader;
        colorbinds.addAll(List.of(binds));
        stageBuffer = WindowFramebuffer.getFloatingPointWindowFramebuffer(binds.length);
    }

    public String getShader() {
        return shader;
    }

    public void render(){
        PostProcessController.currentBuffer = stageBuffer;
        ShaderController.useConfiguration(shader);

        stageBuffer.clearFramebuffer();
        stageBuffer.enableRendering(0,0, stageBuffer.getWidth(), stageBuffer.getHeight());
        PostProcessController.renderable.render();
        stageBuffer.disableRendering();
        for (Tuple<Integer, String> bind : colorbinds) {
            stageBuffer.getTexture(bind.x()).setAsUniform(bind.y());
        }

    }
}
