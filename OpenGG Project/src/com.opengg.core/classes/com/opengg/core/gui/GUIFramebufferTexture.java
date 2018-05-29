package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Framebuffer;

public class GUIFramebufferTexture extends GUIRenderable{
    Framebuffer fb;
    int attachment;

    public GUIFramebufferTexture(Framebuffer buffer, int attachment, Vector2f size){
        Drawable drawn = ObjectCreator.createSquare(new Vector2f(0,0), size, 0.2f);
        this.fb = buffer;
        this.attachment = attachment;
        this.setDrawable(drawn);
    }

    public void render(){
        fb.useTexture(attachment, 0);
        super.render();
    }
}
