/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Warren
 */
public class GUIGroup extends GUIItem {

    Map<String, GUIItem> items = new HashMap<>();

    public GUIGroup(Vector2f pos) {
        this.setPositionOffset(position);
    }

    public void addItem(String name, GUIItem item) {
        item.setParent(this);
        items.put(name, item);
    }

    public GUIItem getItem(String name) {
        return items.get(name);
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
