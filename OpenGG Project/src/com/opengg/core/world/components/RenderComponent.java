/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.shader.VertexArrayFormat;

/**
 *
 * @author Javier
 */
public class RenderComponent extends ComponentHolder implements Renderable{
    Drawable g;
    Matrix4f m = new Matrix4f();
    String shader;
    VertexArrayFormat format;
    boolean transparent;
    
    public RenderComponent(){
        super();
        format = RenderEngine.getDefaultFormat();
        shader = "object";
    };
    
    public RenderComponent(Drawable g){
        this();
        this.g = g;
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
    
    public String getShader() {
        return shader;
    }

    public void setShader(String shader) {
        this.shader = shader;
    }

    public VertexArrayFormat getFormat() {
        return format;
    }

    public void setFormat(VertexArrayFormat format) {
        this.format = format;
    }
    
    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparency(boolean trans) {
        this.transparent = trans;
    }
    
    public void setDrawable(Drawable d){
        this.g = d;
    }
    
    public Drawable getDrawable(){
        return g;
    }
}
