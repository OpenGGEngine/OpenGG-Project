/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.drawn.Drawable;

/**
 *
 * @author Warren
 */
public class GUIRenderable extends GUIItem{ 
    Drawable drawable;
    
    public GUIRenderable(Drawable drawable, Vector2f screenpos){
        this.drawable = drawable;
        this.setPositionOffset(screenpos);
    }
  
    public GUIRenderable(){}
    
    public void setDrawable(Drawable d){
        this.drawable = d;
    }
    
    public void render(){
        if(enabled){
            drawable.setMatrix(Matrix4f.translate(getPosition().x, getPosition().y, layer));
            drawable.render();
        }
        
    }
}
