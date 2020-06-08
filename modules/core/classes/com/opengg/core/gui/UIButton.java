package com.opengg.core.gui;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.gui.layout.DirectionalLayout;
import com.opengg.core.io.input.mouse.MouseButton;
import com.opengg.core.io.input.mouse.MouseButtonListener;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.math.Vector2f;

public class UIButton extends UIPane implements MouseButtonListener{
    private Runnable onClick = () -> {};
    private Runnable onRelease = () -> {};
    private Runnable onClickOutside = () -> {};
    private Vector2f buttonSize;
    private boolean fixedButtonSize = false;

    public UIButton(UIItem background, Runnable onClick){
        this(onClick);
        setBackground(background);
    }


    public UIButton(Runnable onClick){
        super(new DirectionalLayout(DirectionalLayout.Direction.VERTICAL));
        MouseController.onButtonPress(this);
        setOnClick(onClick);
    }

    public UIButton(){
        this(() -> {});
    }

    public UIButton setBackground(UIItem background){
        this.addItem("backOn", background);
        this.repack();
        return this;
    }

    public UIButton setOnClick(Runnable runnable){
        this.onClick = runnable;
        return this;
    }

    public UIButton setOnClickOutside(Runnable runnable){
        this.onClickOutside = runnable;
        return this;
    }

    public UIButton setOnRelease(Runnable runnable){
        this.onRelease = runnable;
        return this;
    }

    public UIButton setOverrideSize(Vector2f size){
        this.buttonSize = size;
        this.fixedButtonSize = true;
        return this;
    }

    private Vector2f getActiveButtonSize(){
        return fixedButtonSize ? buttonSize : getSize();
    }

    private boolean checkIn(Vector2f pos){
        var size = getActiveButtonSize();
        float left   = this.getPosition().x * OpenGG.getWindow().getWidth();
        float right  = (this.getPosition().x + size.x) * OpenGG.getWindow().getWidth();
        float top    = (1-this.getPosition().y) * OpenGG.getWindow().getHeight();
        float bottom = (1-this.getPosition().y - size.y) * OpenGG.getWindow().getHeight();

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
