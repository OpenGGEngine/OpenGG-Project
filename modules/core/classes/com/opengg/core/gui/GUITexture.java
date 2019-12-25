/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;

/**
 *
 * @author Warren
 */
public class GUITexture extends GUIRenderable {
    Texture tex;
    public GUITexture(Texture tex, Vector2f screenpos, Vector2f size) {
        Renderable drawn = ObjectCreator.createSquare(new Vector2f(0,0), size, 0f);
        this.tex = tex;
        this.setDrawable(new TextureRenderable(drawn, tex));
        this.setPositionOffset(screenpos);
    }

    public void setTexture(Texture t){
        ((TextureRenderable)this.d).tex = t;
    }

    public void setSize(Vector2f size){
        Renderable drawn = ObjectCreator.createSquare(new Vector2f(0,0), size, 0f);
        this.setDrawable(new TextureRenderable(drawn, tex));
    }
}
