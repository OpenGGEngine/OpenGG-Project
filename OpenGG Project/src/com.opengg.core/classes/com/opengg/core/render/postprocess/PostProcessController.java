/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.engine.RenderEngine;
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
    private static List<PostProcessingPass> passes = Collections.synchronizedList(new ArrayList<>());
    private static Framebuffer utility;
    static Framebuffer currentBuffer = null;
    static Texture t;
    
    public static void initialize(){
        utility = WindowFramebuffer.getWindowFramebuffer(1);
        drawable = ObjectCreator.createSquare(new Vector2f(-1f,-1f), new Vector2f(1f,1f), -0.5f);
        
        Stage hdr = new Stage("hdr");
        PostProcessingPass hdrpass = new PostProcessingPass(PostProcessingPass.SET, hdr);
        passes.add(hdrpass);
        
        //Stage ssao = new Stage("ssao");
        //PostProcessingPass ssaopass = new PostProcessingPass(ssao, PostProcessingPass.SET);
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
    }
}
