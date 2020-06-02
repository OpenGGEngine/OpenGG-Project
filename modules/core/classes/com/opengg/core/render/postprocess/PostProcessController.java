/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.util.Tuple;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.OpenGLRenderer;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.CommonUniforms;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.WindowFramebuffer;

import java.util.*;

/**
 *
 * @author Javier
 */
public class PostProcessController {
    public static Renderable renderable;
    private static final Map<String, PostProcessingPass> passes = new HashMap<>();
    private static Framebuffer utility;
    static Framebuffer currentBuffer = null;
    static Texture t;
    
    public static void initialize(){
        utility = WindowFramebuffer.getFloatingPointWindowFramebuffer(1);
        renderable = ObjectCreator.createSquare(new Vector2f(0f,0f), new Vector2f(1f,1f), -0.9f);

        Stage ssao = new Stage("ssao");
        PostProcessingPass ssaopass = new PostProcessingPass(PostProcessingPass.SET, ssao);
        //passes.put("ssao", ssaopass);

        int blurpasses = 4;
        Stage[] blurs = new Stage[blurpasses*2+2];
        Stage extract = new Stage("bright", Tuple.of(0,"Ka"), Tuple.of(1,"Kd"));
        Stage blurv = new Stage("blurv");
        Stage blurh = new Stage("blurh");
        blurs[0] = extract;
        for (int i = 0; i < blurpasses; i++) {
            blurs[i*2+1] = blurh;
            blurs[i*2+2] = blurv;
        }
        Stage add = new Stage("add");
        blurs[blurpasses*2+1] = add;

        PostProcessingPass bloom = new PostProcessingPass(PostProcessingPass.SET,
                blurs);
        //passes.put("bloom", bloom);

        Stage hdr = new Stage("hdr");
        PostProcessingPass hdrpass = new PostProcessingPass(PostProcessingPass.SET, hdr);
        passes.put("hdr", hdrpass);

        Stage fxaa = new Stage("fxaa");
        PostProcessingPass fxaapass = new PostProcessingPass(PostProcessingPass.SET, fxaa);
        passes.put("fxaa", fxaapass);

        GGConsole.log("Initialized post processing controller with " + passes.size() + " passes");
    }

    public static void addPass(String name, PostProcessingPass pass){
        passes.put(name, pass);
    }

    public static PostProcessingPass getPass(String name){
        return passes.get(name);
    }
    
    public static void process(Framebuffer initial){
        ((OpenGLRenderer) RenderEngine.renderer).setCulling(false);
        CommonUniforms.setModel(new Matrix4f());
        
        currentBuffer = initial;
        initial.useTexture(0, "Kd");
        initial.useTexture(Framebuffer.DEPTH, "Ka");
        for(PostProcessingPass pass : passes.values()){
            if(!pass.isEnabled()) continue;
            pass.render();
            switch (pass.op) {
                case PostProcessingPass.SET -> currentBuffer.useTexture(0, "Kd");
                case PostProcessingPass.ADD -> {
                    utility.enableRendering();
                    utility.useEnabledAttachments();
                    ShaderController.useConfiguration("add");
                    renderable.render();
                    utility.disableRendering();
                    utility.useTexture(0, "Kd");
                    currentBuffer = utility;
                }
            }
        }

        initial.enableRendering();
        initial.useEnabledAttachments();
        ShaderController.useConfiguration("texture");
        renderable.render();
        initial.disableRendering();
    }

    private PostProcessController() {
    }
}
