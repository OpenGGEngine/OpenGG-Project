package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;

import java.awt.*;

public class GUIProgressBar extends GUIRenderable{
    Vector3f fill;
    Vector3f back;
    float percent;

    public GUIProgressBar(Vector2f pos, Vector2f size, Vector3f fill, Vector3f back){
        percent = 0;
        this.fill = fill;
        this.back = back;

        Drawable drawn = ObjectCreator.createSquare(new Vector2f(0,0), size, 0.2f);

        this.setDrawable(drawn);
        this.setPositionOffset(pos);
    }

    public GUIProgressBar(Vector2f pos, Vector2f size, Color fill, Color back){
        this(pos, size, new Vector3f(fill.getRed(), fill.getGreen(), fill.getBlue()).divide(255f), new Vector3f(back.getRed(), back.getGreen(), back.getBlue()).divide(255f));
    }

    public GUIProgressBar setPercent(float percent){
        this.percent = percent;
        return this;
    }

    public void render(){
        ShaderController.useConfiguration("bar");
        ShaderController.setUniform("fill", fill);
        ShaderController.setUniform("back", back);
        ShaderController.setUniform("percent", percent);
        super.render();
        ShaderController.useConfiguration("gui");

    }
}
