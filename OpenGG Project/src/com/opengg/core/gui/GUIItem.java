/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.drawn.Drawable;

/**
 *
 * @author Warren
 */
public class GUIItem {
    
    public Drawable d;
    Vector2f screenlocalpos;

    public void setPos(Vector2f screenlocalpos) {
        this.screenlocalpos = screenlocalpos;
    }
    
    public GUIItem(Drawable d,Vector2f screenpos){
        this.d = d;
        this.screenlocalpos = screenpos;
    }
    
    public void render(){
        d.render();
    }
}
