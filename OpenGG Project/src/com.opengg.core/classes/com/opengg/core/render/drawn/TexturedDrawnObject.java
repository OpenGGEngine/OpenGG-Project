/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.texture.Texture;

/**
 *
 * @author Warren
 */
public class TexturedDrawnObject implements Drawable{
    public Texture tex;
    Drawable object;
    
    public TexturedDrawnObject(Drawable drawable, Texture t){
        object = drawable;
        this.tex = t;
    }
    
    @Override
    public void render() {
        tex.use(0);
        object.render();
    }

    @Override
    public void setMatrix(Matrix4f m) {
        object.setMatrix(m);
    }

    public void setTexture(Texture tex) {
        this.tex = tex;
    }

}
