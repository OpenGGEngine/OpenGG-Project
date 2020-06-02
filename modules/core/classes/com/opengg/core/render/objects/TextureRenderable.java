/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.objects;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;

/**
 *
 * @author Warren
 */
public class TextureRenderable implements Renderable {
    public Texture tex;
    Renderable object;
    
    public TextureRenderable(Renderable renderable, Texture t){
        object = renderable;
        this.tex = t;
    }
    
    @Override
    public void render() {
        ShaderController.setUniform("Kd", tex);
        object.render();
    }

    public void setTexture(Texture tex) {
        this.tex = tex;
    }

}
