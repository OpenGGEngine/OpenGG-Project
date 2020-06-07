package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;

public class ToggleContainer extends GUIButton{
    private GUIItem toggleableContainer;

    public ToggleContainer(Vector2f size, GUIItem background) {
        super(size, background, () -> {});
        this.setOnClick(() -> {
           if(toggleableContainer == null) return;
           toggleableContainer.setEnabled(!toggleableContainer.isEnabled());
        });
    }

    public ToggleContainer setContents(GUIItem contents){
        this.toggleableContainer = contents;
        return this;
    }
}
