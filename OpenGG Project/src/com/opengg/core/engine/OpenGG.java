/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.render.shader.ShaderController;
import java.net.URL;

/**
 *
 * @author Javier
 */
public class OpenGG {
    public static void initializeOpenGG(){}
    
    public static void initializeRenderEngine(){
        if(EngineInfo.window.getSuccessfulConstruction() && RenderEngine.initialized == false){
            URL verts = ShaderController.class.getResource("glsl/shader.vert");
            URL frags = ShaderController.class.getResource("glsl/shader.frag");
            URL geoms = ShaderController.class.getResource("glsl/shader.geom");
            RenderEngine.init(verts, frags, geoms);
        }
    }
    
    public static void initializeRenderEngine(Object obj){
        if(EngineInfo.window.getSuccessfulConstruction() && RenderEngine.initialized == false){
            URL verts = obj.getClass().getResource("glsl/shader.vert");
            URL frags = obj.getClass().getResource("glsl/shader.frag");
            URL geoms = obj.getClass().getResource("glsl/shader.geom");
            RenderEngine.init(verts, frags, geoms);
        }
    }
    
    public static void initializeAudioController(){
       if(EngineInfo.window.getSuccessfulConstruction() && AudioController.initialized == false){
            AudioController.init();
        }   
    }
    public static void closeEngine(){
        RenderEngine.destroy();
        AudioController.destroy();
    }
}
