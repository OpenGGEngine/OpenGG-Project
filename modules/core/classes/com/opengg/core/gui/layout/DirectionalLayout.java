package com.opengg.core.gui.layout;

import com.opengg.core.math.Vector2f;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DirectionalLayout extends Layout{
    private Direction direction;
    private float padding;
    private float border;
    private boolean invert;

    public DirectionalLayout(Direction direction){
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public DirectionalLayout setPadding(float padding){
        this.padding = padding;
        return this;
    }

    public DirectionalLayout setBorder(float border){
        this.border = border;
        return this;
    }

    public DirectionalLayout invertOrder(boolean invert){
        this.invert = invert;
        return this;
    }

    @Override
    public void reposition() {
        float positionCounter = border;
        var list = invert ? IntStream.range(0, this.parent.getItems().size())
                .map(i -> (this.parent.getItems().size() - 1 - i))	// IntStream
                .mapToObj(this.parent.getItems()::get)				// Stream<T>
                .collect(Collectors.toCollection(ArrayList::new)) : this.parent.getItems();
        for(var child : list){
            var size = child.getSize();
            var negativeXComp = size.x < 0 ? -size.x : 0;
            var negativeYComp = size.y < 0 ? -size.y : 0;
            var offset = switch (direction){
                case VERTICAL -> {
                    child.setPositionOffset(new Vector2f(border + negativeXComp, positionCounter + negativeYComp));
                    yield Math.abs(child.getSize().y) + padding;
                }
                case HORIZONTAL -> {
                    child.setPositionOffset(new Vector2f(positionCounter + negativeXComp, border + negativeYComp));
                    yield Math.abs(child.getSize().x) + padding;
                }
            };
            positionCounter += offset;
        }
    }

    public enum Direction{
        HORIZONTAL, VERTICAL
    }
}
