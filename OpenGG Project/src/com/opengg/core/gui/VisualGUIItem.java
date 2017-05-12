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
public class VisualGUIItem { 
    GUIGroup parent;
    Drawable d;
    Vector2f pos = new Vector2f();

    public void setPos(Vector2f screenlocalpos) {
        this.pos = screenlocalpos;
    }
    public Vector2f getPos() {
        return this.pos;
    }
    
    public VisualGUIItem(Drawable d,Vector2f screenpos){
        this.d = d;
        this.pos = screenpos;
    }
  
    public VisualGUIItem(){}
    
    public void setDrawable(Drawable d){
        this.d = d;
    }
    
    public void render(){
        d.setMatrix(Matrix4f.translate(getPosition().x, getPosition().y, 0));
        d.render();
    }
    
    public void setParent(GUIGroup group){
        this.parent = group;
    }
    
    public Vector2f getPosition(){
        return pos.add(parent.getPosition());
    }
}
