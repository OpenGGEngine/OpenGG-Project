package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Framebuffer;

public class UIFramebufferTexture extends UIRenderable {
    Framebuffer fb;
    int attachment;

    public UIFramebufferTexture(Framebuffer buffer, int attachment, Vector2f size){
        Renderable drawn = ObjectCreator.createSquare(new Vector2f(0,0), size, 0.2f);
        this.fb = buffer;
        this.attachment = attachment;
        this.setRenderable(drawn);
    }

    public void render(){
        fb.getTexture(attachment).setAsUniform("Kd");
        super.render();
    }
}
