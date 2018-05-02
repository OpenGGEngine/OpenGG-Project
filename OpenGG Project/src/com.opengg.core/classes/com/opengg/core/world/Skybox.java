/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;

/**
 *
 * @author Javier
 */
public class Skybox {
    private Drawable d;
    private Texture cube;
    public Skybox(Texture c, float size){
        d = ObjectCreator.createCube(size);
        cube = c;
    }
    
    public Texture getCubemap(){
        return cube;
    }
    
    public Drawable getDrawable(){
        return d;
    }
}
