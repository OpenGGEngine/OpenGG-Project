package com.opengg.core.gui.layout;

import com.opengg.core.gui.GUIGroup;
import com.opengg.core.gui.GUILayoutGroup;

public abstract class Layout {
    protected GUILayoutGroup parent;

    public void setParent(GUILayoutGroup parent){
        this.parent = parent;
    }

    public abstract void reposition();
}
