/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.CommonUniforms;

/**
 *
 * @author Warren
 */
public class GUIRenderable extends GUIItem{ 
    private Renderable renderable;
    
    public GUIRenderable(Renderable renderable, Vector2f screenpos){
        this.renderable = renderable;
        this.setPositionOffset(screenpos);
    }
  
    public GUIRenderable(){}
    
    public void setRenderable(Renderable renderable){
        this.renderable = renderable;
    }

    public Renderable getRenderable(){
        return renderable;
    }
    
    public void render(){
        if(enabled && renderable != null){
            CommonUniforms.setModel(new Matrix4f().translate(new Vector3f(getPosition().x, getPosition().y, layer)));
            renderable.render();
        }
    }
}
