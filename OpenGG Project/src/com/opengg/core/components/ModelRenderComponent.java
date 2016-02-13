/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.components;

import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.texture.Texture;

/**
 *
 * @author Warren
 */
public class ModelRenderComponent implements Component{
    Drawable g;
    Texture normalmap;
    Texture specularmap;
    Texture tex = Texture.blank;
    boolean hasNormalMap = false;
    boolean hasSpecularMap = false;
    public boolean isDrawnObjectGroup = false;
    public ModelRenderComponent(Drawable g){
        this.g = g;
    }
    public void setTexture(Texture d){
        this.tex = d;
    }
    public void setNormalMap(Texture d){
        this.normalmap = d;
        this.hasNormalMap = true;
        
    }
    public void setSpecularMap(Texture d){
        this.normalmap = d;
        this.hasNormalMap = true;
        
    }
    @Override
    public void update() {
       
    }

    @Override
    public void render() {
        if(!isDrawnObjectGroup){
            tex.useTexture(0);
        }
        g.drawShaded();
        
    }
    
}
