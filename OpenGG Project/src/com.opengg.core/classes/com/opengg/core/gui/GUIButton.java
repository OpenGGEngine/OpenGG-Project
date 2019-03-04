package com.opengg.core.gui;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.io.input.mouse.MouseButton;
import com.opengg.core.io.input.mouse.MouseButtonListener;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;

import java.awt.*;

public class GUIButton extends GUIRenderable implements MouseButtonListener{
    final static Texture CLEAR = Texture.ofColor(Color.RED, 0.5f);

    Texture buttonTex;
    Runnable onClick = () -> {};
    Runnable onRelease = () -> {};

    Vector2f dim;

    public GUIButton(Vector2f pos, Vector2f size , Texture texture){
        this.buttonTex = texture;

        TexturedDrawnObject drawn = new TexturedDrawnObject(ObjectCreator.createSquare(new Vector2f(0,0), size,0), buttonTex);
        this.setDrawable(drawn);
        this.dim = size;

        this.setPositionOffset(pos);

        MouseController.onButtonPress(this);
    }

    public GUIButton(Vector2f pos, Vector2f size, Runnable onClick){
        this(pos, size, CLEAR, onClick);
    }

    public GUIButton(Vector2f size, Runnable onClick){
        this(new Vector2f(0,0), size, CLEAR, onClick);
    }

    public GUIButton(Vector2f pos, Vector2f size , Texture texture, Runnable onClick){
        this(pos, size, texture);

        setOnClick(onClick);
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
        float left   = this.getPosition().x * OpenGG.getWindow().getWidth();
        float right  = (this.getPosition().x + this.dim.x) * OpenGG.getWindow().getWidth();
        float top    = (1-this.getPosition().y) * OpenGG.getWindow().getHeight();
        float bottom = (1-this.getPosition().y - this.dim.y) * OpenGG.getWindow().getHeight();

        return pos.x > left && pos.x < right && pos.y < top && pos.y > bottom;
    }

    @Override
    public void onButtonPress(int button) {
        if(!this.isEnabled()) return;
        if(button == MouseButton.LEFT && checkIn(MouseController.get())){
            OpenGG.asyncExec(() -> onClick.run());
        }
    }

    @Override
    public void onButtonRelease(int button) {
        if(!this.isEnabled()) return;
        if(button == MouseButton.LEFT && checkIn(MouseController.get())){
            OpenGG.asyncExec(() -> onRelease.run());
        }
    }
}
