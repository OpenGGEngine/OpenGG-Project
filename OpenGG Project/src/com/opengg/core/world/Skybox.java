/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Cubemap;

/**
 *
 * @author Javier
 */
public class Skybox {
    Drawable d;
    Cubemap cube;
    public Skybox(Cubemap c, float size){
        d = ObjectCreator.createCube(size);
        
    }
}
