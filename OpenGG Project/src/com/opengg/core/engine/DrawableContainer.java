/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.world.components.Renderable;

/**
 *
 * @author Javier
 */
public class DrawableContainer {
    Drawable d;
    Renderable r;
    boolean rd = false;
    
    public DrawableContainer(Drawable d){
        this.d = d;
    }
    
    public DrawableContainer(Renderable r){
        this.r = r;
        rd = true;
    }
    
    public void render(){
        if(rd){
            r.render();
            return;
        }
        d.draw();
    }
    
    public void destroy(){
        if(rd){
            r.getDrawable().destroy();
            return;
        }
        d.destroy();
    }
}
