/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;

import java.util.*;

/**
 *
 * @author Warren
 */
public class UIGroup extends UIItem {
    private int autoGenInt = 0;
    private final Map<String, UIItem> items = new LinkedHashMap<>();

    public UIGroup(){}

    public UIGroup(List<UIItem> items){
        addItems(items);
    }

    public UIGroup addItems(UIItem... items) {
        addItems(Arrays.asList(items));
        return this;
    }

    public UIGroup addItems(List<UIItem> items) {
        items.forEach(this::addItem);
        return this;
    }

    public UIGroup addItem(UIItem item) {
        addItem(Integer.toString(autoGenInt++), item);
        return this;
    }

    public void removeItem(UIItem item) {
        item.setParent(null);
        items.remove(item.getName());
    }

    public void removeItem(String item){
        var found = items.get(item);
        found.setParent(null);
        items.remove(item);
    }

    public UIGroup addItem(String name, UIItem item) {
        item.setParent(this);
        item.setName(name);
        items.put(name, item);
        return this;
    }

    public UIItem getItem(String name) {
        return items.get(name);
    }

    public List<UIItem> getItems() {
        return List.copyOf(items.values());
    }

    public void clear(){
        items.clear();
    }

    @Override
    public Vector2f getSize() {
        float topX = 0, topY = 0;
        for(var child : this.getItems()){
            var realSize = child.getSize().abs();
            var movedSize = realSize.add(child.getPositionOffset());
            if(movedSize.x > topX) topX = movedSize.x;
            if(movedSize.y > topY) topY = movedSize.y;
        }
        return new Vector2f(topX, topY);
    }

    @Override
    public void update(float delta) {
        if (enabled) {
            for (UIItem item : items.values()) {
                item.update(delta);
            }
        }
    }

    @Override
    public void render() {
        if (enabled) {
            for (UIItem item : items.values()) {
                item.render();
            }
        }
    }
}
