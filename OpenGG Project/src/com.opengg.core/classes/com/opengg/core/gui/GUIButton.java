package com.opengg.core.gui;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.io.input.mouse.MouseButton;
import com.opengg.core.io.input.mouse.MouseButtonListener;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;

import java.awt.event.MouseListener;
import java.util.Vector;

public class GUIButton extends GUIRenderable implements MouseButtonListener{

    Texture buttonTex;
    Runnable onClick,onRelease;

    Vector2f dim = new Vector2f();

    public GUIButton(Vector2f dim,Vector2f pos,Texture t){
        this.buttonTex = t;
        TexturedDrawnObject drawn = new TexturedDrawnObject(ObjectCreator.createSquare(new Vector2f(0,0),dim,0),buttonTex);
        this.setDrawable(drawn);
        this.dim = dim;
    }
    @Override
    public void render() {
        super.render();
    }

    public void setOnClick(Runnable runnable){
        this.onClick = runnable;
    }
    public void setOnRelease(Runnable runnable){
        this.onRelease = runnable;
    }

    private boolean checkIn(Vector2f pos){
        float left = this.position.x * OpenGG.getWindow().getWidth();
        float right = (this.position.x + this.dim.x) *OpenGG.getWindow().getWidth();
        float top = (1-this.position.y) * OpenGG.getWindow().getHeight();
        float bottom = (1-this.position.y - this.dim.y) * OpenGG.getWindow().getHeight();
        return pos.x > left && pos.x < right && pos.y < top && pos.y > bottom;
    }
    @Override
    public void onButtonPress(int button) {
        if(button == MouseButton.LEFT && checkIn(MouseController.get())){
            onClick.run();
        }
    }

    @Override
    public void onButtonRelease(int button) {
        if(button == MouseButton.LEFT && checkIn(MouseController.get())){
            onRelease.run();
        }
    }
}
