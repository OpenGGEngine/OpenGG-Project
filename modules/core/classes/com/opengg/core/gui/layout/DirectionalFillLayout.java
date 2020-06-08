package com.opengg.core.gui.layout;

import com.opengg.core.gui.UIItem;
import com.opengg.core.math.Vector2f;

public class DirectionalFillLayout extends DirectionalLayout{
    private float size;

    public DirectionalFillLayout(Direction direction) {
        super(direction);
    }

    public DirectionalFillLayout setFillSize(float size){
        this.size = size;
        return this;
    }

    @Override
    public void pack() {
        setPadding((size-getTotalOccupiedSize())/this.parent.getItems().size());
        super.pack();;
    }

    @Override
    public Vector2f getPreferredSize() {
        setPadding((size-getTotalOccupiedSize())/this.parent.getItems().size());
        return super.getPreferredSize();
    }

    private float getTotalOccupiedSize(){
        return (float) this.parent.getItems().stream()
                .map(UIItem::getSize)
                .mapToDouble(c -> this.getDirection() == Direction.HORIZONTAL ? c.x : c.y)
                .map(Math::abs).sum();
    }
}
