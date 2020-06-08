package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;

import java.awt.*;

public class UIProgressBar extends UIRenderable {
    Vector3f fill;
    Vector3f back;
    float percent;

    public UIProgressBar(Vector2f size, Vector3f fill, Vector3f back){
        percent = 0;
        this.fill = fill;
        this.back = back;

        Renderable drawn = ObjectCreator.createSquare(new Vector2f(0,0), size, 0.2f);

        this.setRenderable(drawn);
    }

    public UIProgressBar(Vector2f size, Color fill, Color back){
        this(size, new Vector3f(fill.getRed(), fill.getGreen(), fill.getBlue()).divide(255f), new Vector3f(back.getRed(), back.getGreen(), back.getBlue()).divide(255f));
    }

    public UIProgressBar setPercent(float percent){
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