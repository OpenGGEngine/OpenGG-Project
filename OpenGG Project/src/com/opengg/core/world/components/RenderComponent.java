/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.Drawable;

/**
 *
 * @author Javier
 */
public class RenderComponent extends ComponentHolder implements Renderable{
    Drawable g;
   
    public RenderComponent(Drawable g){
        this.g = g;
    }

    @Override
    public void render() {
        Matrix4f m = new Matrix4f().translate(getPosition()).scale(getScale());
        g.setMatrix(m);
        g.render();
    }

    public void setDrawable(Drawable d){
        this.g = d;
    }
}
