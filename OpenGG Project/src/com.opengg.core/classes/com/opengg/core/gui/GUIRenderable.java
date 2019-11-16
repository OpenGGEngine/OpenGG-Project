/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.ShaderController;

/**
 *
 * @author Warren
 */
public class GUIRenderable extends GUIItem{ 
    Renderable d;
    
    public GUIRenderable(Renderable d, Vector2f screenpos){
        this.d = d;
        this.setPositionOffset(screenpos);
    }
  
    public GUIRenderable(){}
    
    public void setDrawable(Renderable d){
        this.d = d;
    }

    public Renderable getDrawable(){
        return d;
    }
    
    public void render(){
        if(enabled && d != null){
            ShaderController.setModel(new Matrix4f().translate(new Vector3f(getPosition().x, getPosition().y, layer)));
            d.render();
        }
    }
}
