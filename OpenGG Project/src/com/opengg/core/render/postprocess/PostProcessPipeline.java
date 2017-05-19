/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.objects.ObjectBuffers;
import com.opengg.core.render.texture.Framebuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawBuffer;

/**
 *
 * @author Javier
 */
public class PostProcessPipeline {
    static Framebuffer initial;
    static List<StageSet> sets = new ArrayList<>();
    static Drawable sceneQuad;
    static Stage current;
    static Stage add, mult, set;
    public static void initialize(Framebuffer initial){
        PostProcessPipeline.initial = initial;
        sceneQuad = new DrawnObject(ObjectBuffers.getSquareUI(-1, 1, -1, 1, 1f, 1, false));
        
        Stage ssao = new Stage("ssao");
        StageSet ssaostage = new StageSet(StageSet.SET, 0);
        ssaostage.addStage(ssao);
        //addStage(ssaostage);
        
        Stage hdr = new Stage("hdr");
        StageSet hdrstage = new StageSet(StageSet.SET, 0);
        hdrstage.addStage(hdr);
        addStage(hdrstage);
    }
    
    public static void addStage(StageSet s){
        sets.add(s);
    }
    
    public static void process(){
        glDisable(GL_CULL_FACE);
        
        initial.endTexRender();
        initial.useTexture(0, 0);
        initial.useDepthTexture(1);
        for(StageSet ss : sets){
            ss.render();
            if(ss.func == StageSet.ADD){
                add.use();
                add.finalizeAtLoc(0);
            }
        }
        
        if(current != null)
            current.buffer.blitToBack();
        else
            initial.blitToBack();
        
        glDrawBuffer(GL_BACK);
    }
}
