package com.opengg.core.gui;

import com.opengg.core.gui.layout.Layout;
import com.opengg.core.math.Vector2f;

public class UIPane extends UIGroup {
    private final Layout layout;
    private ResizableElement background;

    public UIPane(Layout layout){
        this.layout = layout;
        layout.setParent(this);
    }

    public UIPane setScalingBackground(ResizableElement background){
        if(!(background instanceof UIItem)) throw new RuntimeException("Cannot add non-UI item as background");
        ((UIItem)background).setName("background-INTERNAL");
        ((UIItem)background).setLayer(-0.1f);
        this.background = background;
        return this;
    }

    public UIPane repack(){
        for(var child : this.getItems()){
            if(child instanceof UIPane pane) pane.repack();
        }

        if(background != null){
            var resizable = (UIItem & ResizableElement) background;
            this.removeItem(resizable);
            layout.pack();
            var size = this.getSize();
            this.addItem("background-INTERNAL", resizable);
            resizable.setTargetSize(size);
        }else{
            layout.pack();
        }

        return this;
    }

    @Override
    public Vector2f getSize() {
        return layout.getPreferredSize();
    }
}
