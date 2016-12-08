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
    boolean comp = false;
    boolean df;
    boolean transparent;
    public void setDistanceField(boolean b){
        df = b;
    }
    
    public boolean getDistanceField(){
        return df;
    }
    
    public DrawableContainer(Drawable d, boolean df, boolean transparent){
        this.d = d;
        this.df = df;
        this.transparent = transparent;
    }
    
    public DrawableContainer(Renderable r, boolean df, boolean transparent){
        this.r = r;
        this.df = df;
        this.transparent = transparent;
        comp = true;
    }
    
    public DrawableContainer(Drawable d){
        this(d,false,false);
    }
    
    public DrawableContainer(Renderable r){
        this(r,false,false);
    }
    
    public void draw(){
        if(comp){
            r.render();
            return;
        }
        d.draw();
    }
}
