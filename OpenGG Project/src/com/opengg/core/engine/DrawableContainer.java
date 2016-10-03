/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.render.drawn.Drawable;

/**
 *
 * @author Javier
 */
public class DrawableContainer {
    Drawable d;
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
    public void draw(){
        d.draw();
    }
}
