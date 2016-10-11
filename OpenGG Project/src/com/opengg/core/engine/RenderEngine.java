/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIItem;
import com.opengg.core.render.buffer.ObjectBuffers;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.shader.Mode;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.FramebufferTexture;
import com.opengg.core.util.GlobalInfo;
import com.opengg.core.world.components.Renderable;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL14.GL_DECR_WRAP;
import static org.lwjgl.opengl.GL14.GL_INCR_WRAP;
import static org.lwjgl.opengl.GL20.glStencilOpSeparate;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

/**
 *
 * @author Javier
 */
public class RenderEngine {
    static ArrayList<DrawableContainer> dlist = new ArrayList<>();
    static boolean shadVolumes = false;
    static ShaderController s;
    static Drawable sceneQuad;
    static Drawable skybox;
    static Cubemap skytex;
    
    static FramebufferTexture sceneTex;
    
    public static void init(){
        s = GlobalInfo.main;
        sceneTex = new FramebufferTexture();
        sceneTex.setupTexToBuffer(GlobalInfo.window.getWidth(), GlobalInfo.window.getHeight());
        
        sceneQuad = new DrawnObject(ObjectBuffers.getSquareUI(-1, 1, -1, 1, 1f, 1, false),12);
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        
        glEnable(GL_TEXTURE_2D);

        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

        glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
    }
    
    public static void addDrawable(DrawableContainer d){
        dlist.add(d);
    }
    
    public static void addDrawable(Drawable d){
        dlist.add(new DrawableContainer(d,false,false));
    }
    
    public static void addRenderable(Renderable r){
        dlist.add(new DrawableContainer(r.getDrawable(), false, false));
    }
    
    public static void addRenderable(Renderable r, boolean df, boolean trans){
        dlist.add(new DrawableContainer(r.getDrawable(), df, trans));
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
    }
    
    public static boolean getShadowsEnabled(){
        return shadVolumes;
    }
    
    public static void drawWorld(){
        
        //sceneTex.startTexRender();
        
        if(shadVolumes){
            glDepthMask(true);
            
            glDisable(GL_DEPTH_TEST);
            glDrawBuffer(GL_NONE);
            s.setMode(Mode.POS_ONLY);
            for(DrawableContainer d : dlist){
                d.draw();
            }
            
            glEnable(GL_STENCIL_TEST);
            
            s.setMode(Mode.SHADOW);
            glDepthMask(false);
            glEnable(GL_DEPTH_CLAMP); 
            glDisable(GL_CULL_FACE);
            glStencilFunc(GL_ALWAYS, 0, 0xff);
            glStencilOpSeparate(GL_BACK, GL_KEEP, GL_INCR_WRAP, GL_KEEP);
            glStencilOpSeparate(GL_FRONT, GL_KEEP, GL_DECR_WRAP, GL_KEEP); 
            
            for(DrawableContainer d : dlist){
                d.draw();
            }
            
            glDisable(GL_DEPTH_CLAMP);
            glEnable(GL_CULL_FACE); 
            glStencilFunc(GL_EQUAL, 0x0, 0xFF);
            glStencilOpSeparate(GL_BACK, GL_KEEP, GL_KEEP, GL_KEEP);
            //sceneTex.drawColorAttachment();
            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);
            glDrawBuffer(GL_BACK);
            
        }else{
            
            glDisable(GL_STENCIL_TEST);
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glEnable(GL_CULL_FACE); 
            
        }
        
        
        s.setMode(Mode.OBJECT);
        for(DrawableContainer d : dlist){
            if(d.getDistanceField()){
                s.setDistanceField(true);
                d.draw();
                s.setDistanceField(false);
                continue;
            }
            d.draw();
        }

        glDisable(GL_CULL_FACE); 
        s.setMode(Mode.SKYBOX);
        skytex.use(0);
        skybox.draw();
        glEnable(GL_CULL_FACE); 
        
        if(shadVolumes){
            glDisable(GL_STENCIL_TEST);
        }
        /*
        sceneTex.endTexRender();
        glDisable(GL_CULL_FACE);
        GUI.startGUIPos();
        s.setMode(Mode.PP);
        sceneTex.useTexture(0);
        sceneTex.useDepthTexture(1);
        sceneQuad.draw();
        
        s.setDistanceField(true);
        GUI.enableGUI();
        GUI.render();
        s.setDistanceField(false);
                */
    }
}
