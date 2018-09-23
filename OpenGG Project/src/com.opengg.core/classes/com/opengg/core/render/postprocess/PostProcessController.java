/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.math.Tuple;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.objects.ObjectCreator;
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
    public static Drawable drawable;
    private static final Map<String, PostProcessingPass> passes = new HashMap<>();
    private static Framebuffer utility;
    static Framebuffer currentBuffer = null;
    static Texture t;
    
    public static void initialize(){
        utility = WindowFramebuffer.getFloatingPointWindowFramebuffer(1);
        drawable = ObjectCreator.createSquare(new Vector2f(0f,0f), new Vector2f(1f,1f), -0.9f);

        Stage ssao = new Stage("ssao");
        PostProcessingPass ssaopass = new PostProcessingPass(PostProcessingPass.SET, ssao);
        //passes.put("ssao", ssaopass);

        int blurpasses = 4;
        Stage[] blurs = new Stage[blurpasses*2+2];
        Stage extract = new Stage("bright", new Tuple<>(0,1), new Tuple<>(1,0));
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
        passes.put("bloom", bloom);

        Stage hdr = new Stage("hdr");
        PostProcessingPass hdrpass = new PostProcessingPass(PostProcessingPass.SET, hdr);
        passes.put("hdr", hdrpass);

        Stage fxaa = new Stage("fxaa");
        PostProcessingPass fxaapass = new PostProcessingPass(PostProcessingPass.SET, fxaa);
        passes.put("fxaa", fxaapass);
    }

    public static void addPass(String name, PostProcessingPass pass){
        passes.put(name, pass);
    }

    public static PostProcessingPass getPass(String name){
        return passes.get(name);
    }
    
    public static void process(Framebuffer initial){
        RenderEngine.setCulling(false);
        
        currentBuffer = initial;
        initial.useTexture(0, 0);
        initial.useTexture(Framebuffer.DEPTH, 1);
        for(PostProcessingPass pass : passes.values()){
            if(!pass.isEnabled()) continue;
            pass.render();
            switch(pass.op){
                case PostProcessingPass.SET:
                    currentBuffer.useTexture(0, 0);
                    break;
                case PostProcessingPass.ADD:
                    utility.enableRendering();
                    utility.useEnabledAttachments();
                    ShaderController.useConfiguration("add");
                    drawable.render();
                    utility.disableRendering();
                    utility.useTexture(0, 0);
                    currentBuffer = utility;
            }
        }

        initial.enableRendering();
        initial.useEnabledAttachments();
        ShaderController.useConfiguration("texture");
        drawable.render();
        initial.disableRendering();


    }

    private PostProcessController() {
    }
}
