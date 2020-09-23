package com.opengg.core.gui;

import java.util.function.Consumer;

public class ToggleButton extends UIButton {
    private UIItem toggleableContainer;
    private Consumer<Boolean> toggleFunc = (b) ->{};
    private boolean on = false;

    public ToggleButton(UIItem background) {
        super(background, () -> {});
        this.setOnClick(() -> {
            on = !on;
            if(toggleableContainer != null) toggleableContainer.setEnabled(on);
            toggleFunc.accept(on);
        });
    }

    public ToggleButton setOnToggleContents(UIItem contents){
        this.toggleableContainer = contents;

        return this;
    }

    public ToggleButton setToggleCommand(Consumer<Boolean> toggleFunc) {
        this.toggleFunc = toggleFunc;
        return this;
    }
}
