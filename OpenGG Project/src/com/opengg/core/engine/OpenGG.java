/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.util.GlobalInfo;
import java.net.URL;

/**
 *
 * @author Javier
 */
public class OpenGG {
    static WorldEngine e;
    static TextureManager tex;
    
    public static void initializeOpenGG(){
        e = new WorldEngine();
    }
    
    public static void initializeRenderEngine(){
        if(GlobalInfo.window.getSuccessfulConstruction() && RenderEngine.initialized == false){
            URL verts = ShaderController.class.getResource("glsl/shader.vert");
            URL frags = ShaderController.class.getResource("glsl/shader.frag");
            URL geoms = ShaderController.class.getResource("glsl/shader.geom");
            RenderEngine.init(verts, frags, geoms);
        }
    }
    public static void initializeAudioController(){
       if(GlobalInfo.window.getSuccessfulConstruction() && RenderEngine.initialized == false){
            AudioController.init();
        }   
    }
    public static void closeEngine(){
        RenderEngine.destroy();
        AudioController.destroy();
    }
}
