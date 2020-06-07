package com.opengg.core.gui.layout;

import com.opengg.core.gui.GUIItem;
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
    public void reposition() {
        float totalOccupiedSize = (float) this.parent.getItems().stream()
                .map(GUIItem::getSize)
                .mapToDouble(c -> this.getDirection() == Direction.HORIZONTAL ? c.x : c.y)
                .map(Math::abs).sum();
        setPadding((size-totalOccupiedSize)/this.parent.getItems().size());
        super.reposition();;
    }

}
