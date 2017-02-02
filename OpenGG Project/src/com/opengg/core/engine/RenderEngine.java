/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIItem;
import com.opengg.core.model.ModelManager;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.VertexArrayObject;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.postprocess.PostProcessPipeline;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.components.ModelRenderComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DECR_WRAP;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.GL_INCR_WRAP;
import static org.lwjgl.opengl.GL14.GL_MAX;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL20.glStencilOpSeparate;
import static org.lwjgl.opengl.GL30.GL_MAJOR_VERSION;
import static org.lwjgl.opengl.GL30.GL_MINOR_VERSION;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

/**
 *
 * @author Javier
 */
public class RenderEngine {
    static List<RenderGroup> groups = new ArrayList<>();
    static List<Light> lights = new ArrayList<>();
    static RenderGroup dlist;
    static RenderGroup adjdlist;
    static boolean shadVolumes = false;
    static Drawable skybox;
    static Cubemap skytex;
    static boolean initialized;
    static Framebuffer sceneTex;
    static VertexArrayObject vao;
    static boolean cull = true;
    
    static boolean init(){
        vao = new VertexArrayObject();
        vao.bind();
        
        ShaderController.initialize();
        TextureManager.initialize();
        ModelManager.initialize();
        sceneTex = Framebuffer.getFramebuffer(OpenGG.window.getWidth(), OpenGG.window.getHeight(), 2);
        PostProcessPipeline.initialize(sceneTex);
        
        dlist = new RenderGroup();
        adjdlist = new RenderGroup();
        adjdlist.setAdjacencyMesh(true);
        
        groups.add(dlist);
        groups.add(adjdlist);
        
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
        return true;
    }
    
    public static String getGLVersion(){
        return glGetInteger(GL_MAJOR_VERSION) + "." + glGetInteger(GL_MINOR_VERSION);
    }
    
    public static void setWireframe(boolean wf){
        if(wf)
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        else
            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
    }
    
    public static void addLight(Light l){
        lights.add(l);
    }
    
    public static void addRenderGroup(RenderGroup r){
        groups.add(r);
    }
    
    public List<RenderGroup> getRenderGroups(){
        return groups;
    }
    
    public static void addRenderable(Renderable r){
        if(r instanceof ModelRenderComponent){
            adjdlist.add(r);
        }else{
            dlist.add(r);
        }
    }
    
    public static void setSkybox(Drawable sky, Cubemap c){
        skybox = sky;
        skytex = c;
    }    
    
    public static void addGUIItem(GUIItem g){
        GUI.addItem(g);
    }
    
    public static void setShadowVolumes(boolean vol){
        shadVolumes = vol;
        System.out.println(vol);
    }
    
    public static void setCulling(boolean enable){
        cull = enable;
    }
    
    public static boolean getShadowsEnabled(){
        return shadVolumes;
    }
    
    public void useLight(Light l, int loc){
        
    }
    
    public void useLight(Light l){
        useLight(l,0);
    }
    
    private static void writeToDepth(){
        glDepthMask(true);
        glDrawBuffer(GL_NONE);
        ShaderController.useConfiguration("adjpassthrough");
        groups.stream().filter(group -> group.adj).forEach((group) -> {
            group.render();
        });
    }
    
    private static void cullShadowFaces(){
        glEnable(GL_STENCIL_TEST);

        glDepthMask(false);
        glEnable(GL_DEPTH_CLAMP); 
        glDisable(GL_CULL_FACE);
        
        glStencilFunc(GL_ALWAYS, 0, 0xff);
        glStencilOpSeparate(GL_BACK, GL_KEEP, GL_INCR_WRAP, GL_KEEP);
        glStencilOpSeparate(GL_FRONT, GL_KEEP, GL_DECR_WRAP, GL_KEEP); 
        
        ShaderController.useConfiguration("volume");
        groups.stream().filter(group -> group.adj).forEach((group) -> {
            group.render();
        });

        glDisable(GL_DEPTH_CLAMP);
        glEnable(GL_CULL_FACE); 
        
    }
    
    public static void sortOrders(){
        groups = groups.stream().sorted((RenderGroup o1, RenderGroup o2) -> {
            if(o1.getOrder() > o2.getOrder()){
                return 1;
            }else if(o1.getOrder() < o2.getOrder()){
                return -1;
            }else{
                return 0;
            }
        }).collect(Collectors.toList());
    }
    
    public static void draw(){
        sceneTex.startTexRender();
        if(shadVolumes){
            writeToDepth();   
            cullShadowFaces();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glStencilFunc(GL_EQUAL, 0x0, 0xFF);
            glStencilOpSeparate(GL_BACK, GL_KEEP, GL_KEEP, GL_KEEP);
            glStencilOpSeparate(GL_FRONT, GL_KEEP, GL_KEEP, GL_KEEP);
        }else{
            glDisable(GL_STENCIL_TEST);
        }
        
        sceneTex.enableColorAttachments();
        resetConfig();
        
        for(RenderGroup d : groups){
            ShaderController.setDistanceField(d.isText());
            ShaderController.setMode(d.getMode());
            if(d.hasAdjacencyMesh()){
                ShaderController.useConfiguration("adjobject");
            }else{
                ShaderController.useConfiguration("object");
            }
            d.render(); 
            
        }
        
        glDisable(GL_STENCIL_TEST);
        
        if(shadVolumes){
            glDepthFunc(GL_LESS);
            
            glBlendEquation(GL_MAX);
            glBlendFunc(GL_ONE, GL_ONE);

            for(RenderGroup d : groups){
                ShaderController.setDistanceField(d.isText());
                ShaderController.setMode(d.getMode());
                if(d.hasAdjacencyMesh()){
                   ShaderController.useConfiguration("adjpassthrough");
                }else{
                    ShaderController.useConfiguration("ambient");
                }
                d.render();
            }
        }
        
        resetConfig();
        
        glDisable(GL_CULL_FACE); 
        ShaderController.useConfiguration("sky");
        skytex.use(0);
        skybox.render();    
        GUI.startGUIPos();
        
        PostProcessPipeline.process();
        
        ShaderController.useConfiguration("object");
        ShaderController.setDistanceField(true);
        GUI.enableGUI();
        GUI.render();
        ShaderController.setDistanceField(false);
        
    }
    
    public static void resetConfig(){
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glDisable(GL_CULL_FACE);
    }
    
    static void destroy(){
        TextureManager.destroy();
        GGConsole.log("Render engine has finalized");
    }
}
