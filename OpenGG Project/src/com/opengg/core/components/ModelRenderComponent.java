/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.components;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.GlobalInfo;
import com.opengg.core.world.WorldObject;

/**
 *
 * @author Warren
 */
public class ModelRenderComponent implements Component{
    Drawable g;
    private Vector3f offset = new Vector3f(0,0,0);
    private Vector3f rotationoffset = new Vector3f(0,0,0);
    WorldObject w;
   
    public ModelRenderComponent(Drawable g){
        this.g = g;
    }
   
    public void setOffset(Vector3f f){
        this.offset = f;
    }
    public void setRotOffset(Vector3f rot){
        this.rotationoffset = rot;
    }
    @Override
    public void update() {
       
    }

    @Override
    public void render() {
        
        
        g.drawShaded();
        
        
    }

    
    
}
