package com.opengg.core.gui.layout;

import com.opengg.core.gui.UIPane;
import com.opengg.core.math.Vector2f;

public abstract class Layout {
    protected UIPane parent;

    public void setParent(UIPane parent){
        this.parent = parent;
    }

    public abstract Vector2f getPreferredSize();

    public abstract void pack();
}
