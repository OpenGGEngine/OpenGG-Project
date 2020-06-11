package com.opengg.core.gui;

public class ToggleButton extends UIButton {
    private UIItem toggleableContainer;

    public ToggleButton(UIItem background) {
        super(background, () -> {});
        this.setOnClick(() -> toggleableContainer.setEnabled(!toggleableContainer.isEnabled()));
    }

    public ToggleButton setContents(UIItem contents){
        this.toggleableContainer = contents;

        return this;
    }
}
