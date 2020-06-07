package com.opengg.core.gui;

import com.opengg.core.gui.layout.Layout;

import java.util.List;

public class GUILayoutGroup extends GUIGroup{
    private Layout layout;

    public GUILayoutGroup(Layout layout){
        this.layout = layout;
        layout.setParent(this);
    }

    public GUILayoutGroup repack(){
        layout.reposition();
        return this;
    }
}
