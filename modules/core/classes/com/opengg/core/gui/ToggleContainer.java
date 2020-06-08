package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;

public class ToggleContainer extends UIButton {
    private UIItem toggleableContainer;

    public ToggleContainer(Vector2f size, UIItem background) {
        super(background, () -> {});
        this.setOnClick(() -> {
           if(toggleableContainer == null) return;
           toggleableContainer.setEnabled(!toggleableContainer.isEnabled());
        });
    }

    public ToggleContainer setContents(UIItem contents){
        this.toggleableContainer = contents;
        return this;
    }
}
