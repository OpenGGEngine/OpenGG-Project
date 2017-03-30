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
    Matrix4f m = new Matrix4f();
    
    public RenderComponent(){};
    
    public RenderComponent(Drawable g){
        super();
        this.g = g;
        System.out.println(g.getClass().getCanonicalName());
    }

    @Override
    public void render() {
        if(g != null){
            g.setMatrix(m);
            g.render();
        }

    }

    @Override
    public void update(float delta){
        m = new Matrix4f().translate(getPosition()).rotateQuat(getRotation()).scale(getScale());
    }
    
    public void setDrawable(Drawable d){
        this.g = d;
    }
    
    public Drawable getDrawable(){
        return g;
    }
}
