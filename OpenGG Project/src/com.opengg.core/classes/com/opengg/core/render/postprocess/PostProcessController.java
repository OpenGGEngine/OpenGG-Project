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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Javier
 */
public class PostProcessController {
    public static Drawable drawable;
    private static final List<PostProcessingPass> passes = Collections.synchronizedList(new ArrayList<>());
    private static Framebuffer utility;
    static Framebuffer currentBuffer = null;
    static Texture t;
    
    public static void initialize(){
        utility = WindowFramebuffer.getFloatingPointWindowFramebuffer(1);
        drawable = ObjectCreator.createSquare(new Vector2f(-1f,-1f), new Vector2f(1f,1f), -0.9f);

        Stage extract = new Stage("bright", new Tuple<>(0,1), new Tuple<>(1,0));
        Stage blurv = new Stage("blurh");
        Stage blurv2 = new Stage("blurh");
        Stage blurv3 = new Stage("blurh");
        Stage blurv4 = new Stage("blurh");
        Stage blurh = new Stage("blurv");
        Stage blurh2 = new Stage("blurv");
        Stage blurh3 = new Stage("blurv");
        Stage blurh4 = new Stage("blurv");
        Stage add = new Stage("add");
        PostProcessingPass blurpass = new PostProcessingPass(PostProcessingPass.SET,
                extract, blurh, blurh2, blurh3, blurh4, blurv, blurv2, blurv3, blurv4, add);
        passes.add(blurpass);

        Stage hdr = new Stage("hdr");
        PostProcessingPass hdrpass = new PostProcessingPass(PostProcessingPass.SET, hdr);
        passes.add(hdrpass);

        Stage ssao = new Stage("ssao");
        PostProcessingPass ssaopass = new PostProcessingPass(PostProcessingPass.SET, ssao);
        //passes.add(ssaopass);





    }

    public static void addPass(PostProcessingPass pass){
        passes.add(pass);
    }
    
    public static void process(Framebuffer initial){
        RenderEngine.setCulling(false);
        
        currentBuffer = initial;
        initial.useTexture(0, 0);
        initial.useTexture(Framebuffer.DEPTH, 1);
        for(PostProcessingPass pass : passes){
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
        
        currentBuffer.blitToBack();
        RenderEngine.setCulling(true);
    }

    private PostProcessController() {
    }
}
