/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.internal.opengl.OpenGLRenderer;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.CommonUniforms;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.WindowFramebuffer;
import com.opengg.core.render.window.WindowController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Javier
 */
public class PostProcessController {
    public static Renderable renderable;
    private static final Map<String, PostProcessStage> stages = new LinkedHashMap<>();
    private static final Map<String, Framebuffer> buffers = new HashMap<>();

    public static void initialize(){
        renderable = ObjectCreator.createSquare(new Vector2f(0f,0f), new Vector2f(1f,1f), -0.9f);

        buffers.put("output", WindowFramebuffer.getFloatingPointWindowFramebuffer(1));
        buffers.put("gbuffer2", WindowFramebuffer.getFloatingPointWindowFramebuffer(1));
        buffers.put("brightness", WindowFramebuffer.getFloatingPointWindowFramebuffer(1));
        buffers.put("blur", WindowFramebuffer.getFloatingPointWindowFramebuffer(1));
        buffers.put("bloom", WindowFramebuffer.getFloatingPointWindowFramebuffer(1));

        RenderStage bright = new RenderStage("bright", List.of(new RenderStage.InputBuffer("gbuffer", 0, "Kd")), "brightness");
        addPass("brightness", bright);

        RenderStage blurvBloom = new RenderStage("blur",
                List.of(new RenderStage.InputBuffer("brightness", 0, "Kd")), "blur",
                () -> ShaderController.setUniform("direction", new Vector2f(2,0)));
        addPass("blurv", blurvBloom);

        RenderStage blurhBloom = new RenderStage("blur",
                List.of(new RenderStage.InputBuffer("blur", 0, "Kd")), "bloom",
                () -> ShaderController.setUniform("direction", new Vector2f(0,2)));
        addPass("blurh", blurhBloom);

        RenderStage addBlur = new RenderStage("add",
                List.of(new RenderStage.InputBuffer("bloom", 0, "Kd"),
                        new RenderStage.InputBuffer("gbuffer", 0, "Ka")), "gbuffer2");
        addPass("addBlur", addBlur);

        RenderStage hdr = new RenderStage("hdr", List.of(new RenderStage.InputBuffer("gbuffer", 0, "Kd")), "output");
        addPass("hdr", hdr);



        GGConsole.log("Initialized post processing controller with " + stages.size() + " passes");
    }

    public static void addPass(String name, PostProcessStage stage){
        stages.put(name, stage);
    }

    public static Framebuffer getBuffer(String name){
        return buffers.get(name);
    }
    
    public static Framebuffer process(Framebuffer initialBuffer){
        ((OpenGLRenderer) RenderEngine.renderer).setBackfaceCulling(false);
        CommonUniforms.setModel(Matrix4f.IDENTITY);
        ShaderController.setUniform("resolution", new Vector2f(WindowController.getWidth(), WindowController.getHeight()));

        buffers.put("gbuffer", initialBuffer);
        initialBuffer.getTexture(0).setAsUniform("Kd");
        initialBuffer.getTexture(Framebuffer.DEPTH).setAsUniform("Ka");
        for(var stage : stages.values()){
            stage.render();
        }

        return buffers.get("output");
    }

    private PostProcessController() {
    }
}
