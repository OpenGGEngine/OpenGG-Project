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
    Drawable d;
    
    public GUIRenderable(Drawable d,Vector2f screenpos){
        this.d = d;
        this.setPositionOffset(screenpos);
    }
  
    public GUIRenderable(){}
    
    public void setDrawable(Drawable d){
        this.d = d;
    }
    
    public void render(){
        if(enabled){
            d.setMatrix(Matrix4f.translate(getPosition().x, getPosition().y, layer));
            d.render();
        }
        
    }
}
