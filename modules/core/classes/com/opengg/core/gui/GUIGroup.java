/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Warren
 */
public class GUIGroup extends GUIItem {
    private int autoGenInt = 0;
    private final Map<String, GUIItem> items = new HashMap<>();

    public GUIGroup(){}

    public GUIGroup(List<GUIItem> items){
        addItems(items);
    }

    public GUIGroup addItems(GUIItem... items) {
        addItems(Arrays.asList(items));
        return this;
    }

    public GUIGroup addItems(List<GUIItem> items) {
        items.forEach(this::addItem);
        return this;
    }

    public GUIGroup addItem(GUIItem item) {
        addItem(Integer.toString(autoGenInt++), item);
        return this;
    }

    public GUIGroup addItem(String name, GUIItem item) {
        item.setParent(this);
        item.setName(name);
        items.put(name, item);
        return this;
    }

    public GUIItem getItem(String name) {
        return items.get(name);
    }

    public void clear(){
        items.clear();
    }

    @Override
    public void update(float delta) {
        if (enabled) {
            for (GUIItem item : items.values()) {
                item.update(delta);
            }
        }
    }

    @Override
    public void render() {
        if (enabled) {
            for (GUIItem item : items.values()) {
                item.render();
            }
        }
    }
}
