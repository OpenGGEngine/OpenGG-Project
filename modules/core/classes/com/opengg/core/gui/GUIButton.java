package com.opengg.core.gui;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.io.input.mouse.MouseButton;
import com.opengg.core.io.input.mouse.MouseButtonListener;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.objects.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;

public class GUIButton extends GUIGroup implements MouseButtonListener{
    private Runnable onClick = () -> {};
    private Runnable onRelease = () -> {};
    private Runnable onClickOutside = () -> {};
    private Vector2f buttonSize;

    public GUIButton(Vector2f size , GUIItem background, Runnable onClick){
        this(size, onClick);
        setBackground(background);
    }


    public GUIButton(Vector2f size, Runnable onClick){
        this.buttonSize = size;
        MouseController.onButtonPress(this);
        setOnClick(onClick);
    }

    public GUIButton(Vector2f size){
        this(size, () -> {});
    }

    public GUIButton setBackground(GUIItem background){
        this.addItem("backOn", background);
        return this;
    }

    @Override
    public void render() {
        super.render();
    }

    public GUIButton setOnClick(Runnable runnable){
        this.onClick = runnable;
        return this;
    }

    public GUIButton setOnClickOutside(Runnable runnable){
        this.onClickOutside = runnable;
        return this;
    }

    public GUIButton setOnRelease(Runnable runnable){
        this.onRelease = runnable;
        return this;
    }

    private boolean checkIn(Vector2f pos){
        float left   = this.getPosition().x * OpenGG.getWindow().getWidth();
        float right  = (this.getPosition().x + this.buttonSize.x) * OpenGG.getWindow().getWidth();
        float top    = (1-this.getPosition().y) * OpenGG.getWindow().getHeight();
        float bottom = (1-this.getPosition().y - this.buttonSize.y) * OpenGG.getWindow().getHeight();

        return pos.x > left && pos.x < right && pos.y < top && pos.y > bottom;
    }

    @Override
    public void onButtonPress(int button) {
        if(!this.isEnabled()) return;
        if(button == MouseButton.LEFT && checkIn(MouseController.get())){
            OpenGG.asyncExec(() -> onClick.run());
        }else if(button == MouseButton.LEFT){
            onClickOutside.run();
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
