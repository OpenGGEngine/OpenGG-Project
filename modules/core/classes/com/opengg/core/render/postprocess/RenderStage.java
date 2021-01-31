/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.shader.ShaderController;

import java.util.List;

/**
 *
 * @author Javier
 */
public class RenderStage implements PostProcessStage{
    private final List<InputBuffer> inputs;
    private final String output;
    private final String shader;
    private final Runnable preRun;

    public RenderStage(String shader, List<InputBuffer> inputs, String output){
        this(shader, inputs, output, () -> {});
    }

    public RenderStage(String shader, List<InputBuffer> inputs, String output, Runnable preRun){
        this.shader = shader;
        this.inputs = inputs;
        this.output = output;
        this.preRun = preRun;
    }

    public String getShader() {
        return shader;
    }

    @Override
    public void render(){
        ShaderController.useConfiguration(shader);

        for (var input : inputs) {
            PostProcessController.getBuffer(input.buffer).getTexture(input.texture).setAsUniform(input.uniform);
        }

        preRun.run();

        var writeBuffer = PostProcessController.getBuffer(output);

        writeBuffer.clearFramebuffer(new Vector3f());
        writeBuffer.enableRendering(0,0, writeBuffer.getWidth(), writeBuffer.getHeight());
        PostProcessController.renderable.render();
        writeBuffer.disableRendering();
    }

    record InputBuffer(String buffer, int texture, String uniform){

    }
}
