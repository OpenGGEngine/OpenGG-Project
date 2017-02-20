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
public class RenderComponent extends Component implements Renderable{
    Drawable g;
   
    public RenderComponent(){};
    
    public RenderComponent(Drawable g){
        super();
        this.g = g;
    }

    @Override
    public void render() {
        Matrix4f m = new Matrix4f().translate(getPosition()).scale(getScale());
        if(g != null){
            g.setMatrix(m);
            g.render();
        }
        
    }

    public void setDrawable(Drawable d){
        this.g = d;
    }
}
