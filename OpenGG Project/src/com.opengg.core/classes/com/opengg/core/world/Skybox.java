/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
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

    public Skybox(Texture tex, float size){
        d = ObjectCreator.createCube(size);
        cube = tex;
    }

    public void setCubemapMatrix(Matrix4f mat){
        d.setMatrix(mat);
    }

    public Texture getCubemap(){
        return cube;
    }
    
    public Drawable getDrawable(){
        return d;
    }
}
