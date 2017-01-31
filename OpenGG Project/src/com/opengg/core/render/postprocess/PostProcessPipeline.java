/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.postprocess;

import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.objects.ObjectBuffers;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Framebuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.glDrawBuffer;

/**
 *
 * @author Javier
 */
public class PostProcessPipeline {
    static Framebuffer initial;
    static List<Stage> stages = new ArrayList<>();
    static Drawable sceneQuad;
    public static void initialize(Framebuffer initial){
        PostProcessPipeline.initial = initial;
        sceneQuad = new DrawnObject(ObjectBuffers.getSquareUI(-1, 1, -1, 1, 1f, 1, false));
        //addStage(new Stage("pp", 0));
    }
    
    public static void addStage(Stage s){
        stages.add(s);
    }
    
    public static void process(){
        initial.endTexRender();
        initial.useTexture(0, 0);
        initial.useTexture(1, 1);
        for(Stage s : stages){
            ShaderController.useConfiguration(s.shader);
            s.buffer.enableColorAttachments();
            s.buffer.startTexRender();
            sceneQuad.draw();
            s.buffer.endTexRender();
            s.buffer.useTexture(0);
        }
        glDrawBuffer(GL_BACK);
        ShaderController.useConfiguration("texture");
        sceneQuad.draw();
    }
}
