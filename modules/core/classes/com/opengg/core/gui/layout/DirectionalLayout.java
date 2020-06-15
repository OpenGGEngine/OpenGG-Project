package com.opengg.core.gui.layout;

import com.opengg.core.gui.UIItem;
import com.opengg.core.math.Vector2f;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DirectionalLayout extends Layout{
    private final Direction direction;
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

    public float getBorder() {
        return border;
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
    public Vector2f getPreferredSize() {
        float positionCounter = 0;
        float otherDirectionCounter = 0;
        for(var child : this.parent.getItems().stream().filter(UIItem::isLocallyEnabled).collect(Collectors.toList())){
            var offsetInDir = switch (direction){
                case VERTICAL -> Math.abs(child.getSize().y) + padding;
                case HORIZONTAL -> Math.abs(child.getSize().x) + padding;
            };
            var offsetInOther = switch (direction){
                case VERTICAL -> Math.abs(child.getSize().x);
                case HORIZONTAL -> Math.abs(child.getSize().y);
            };
            positionCounter += offsetInDir;
            otherDirectionCounter = Math.max(offsetInOther, otherDirectionCounter);
        }

        positionCounter -= padding;
        positionCounter += border * 2;
        otherDirectionCounter += border * 2;
        return switch (direction){
            case VERTICAL -> new Vector2f(otherDirectionCounter, positionCounter);
            case HORIZONTAL -> new Vector2f(positionCounter, otherDirectionCounter);
        };
    }

    @Override
    public void pack() {
        float positionCounter = border;
        var list = invert ? IntStream.range(0, this.parent.getItems().size())
                .map(i -> (this.parent.getItems().size() - 1 - i))	// IntStream
                .mapToObj(this.parent.getItems()::get)
                .filter(UIItem::isLocallyEnabled)// Stream<T>
                .collect(Collectors.toCollection(ArrayList::new))
                : this.parent.getItems().stream().filter(UIItem::isLocallyEnabled).collect(Collectors.toList());
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
