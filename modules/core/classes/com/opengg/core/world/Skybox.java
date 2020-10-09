/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.render.Renderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;

/**
 *
 * @author Javier
 */
public class Skybox {
    private final Renderable d;
    private final Texture cube;

    public Skybox(Texture tex, float size){
        d = ObjectCreator.createCube(size);
        cube = tex;
    }

    public Texture getCubemap(){
        return cube;
    }
    
    public Renderable getDrawable(){
        return d;
    }
}
