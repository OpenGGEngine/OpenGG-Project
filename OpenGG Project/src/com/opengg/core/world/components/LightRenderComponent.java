/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.objects.ObjectCreator;

/**
 *
 * @author Javier
 */
public class LightRenderComponent extends RenderComponent{
    Light l;  
    
    public LightRenderComponent(Drawable g) {
        super(g);
    }
    
    public LightRenderComponent(Light l){
        super(ObjectCreator.createCube(1));
        this.l = l;
    }

    @Override
    public Vector3f getPosition(){
        return l.pos;
    } 
}
