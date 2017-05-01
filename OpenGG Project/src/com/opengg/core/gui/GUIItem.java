/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.shader.ShaderController;

/**
 *
 * @author Warren
 */
public class GUIItem {
    
    GUIGroup parent;
    
    public Drawable d;
    Vector2f screenlocalpos;

    public void setPos(Vector2f screenlocalpos) {
        this.screenlocalpos = screenlocalpos;
    }
    public Vector2f getPos() {
        return this.screenlocalpos;
    }
    
    public GUIItem(Drawable d,Vector2f screenpos){
        this.d = d;
        this.screenlocalpos = screenpos;
    }
  
    
    public void render(Vector2f local){
       // ShaderController.setModel(Matrix4f.translate(this.screenlocalpos.x + local.x
       //         , this.screenlocalpos.y + local.y, 0));
        d.setMatrix(Matrix4f.translate(this.screenlocalpos.x + local.x
                , this.screenlocalpos.y + local.y, 0));
        d.render();
    }
}
