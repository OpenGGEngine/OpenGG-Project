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

    public GUIGroup(){
        this(new Vector2f(0,0));
    }

    public GUIGroup(Vector2f pos) {
        this.setPositionOffset(pos);
    }

    public void addItem(String name, GUIItem item) {
        item.setParent(this);
        item.setName(name);
        items.put(name, item);
    }

    public GUIItem getItem(String name) {
        return items.get(name);
    }

    public void clear(){items.clear();}


    @Override
    public void render() {
        if (enabled) {
            for (GUIItem item : items.values()) {
                item.render();
            }
        }
    }

    @Override
    public void update(float delta) {
        if (enabled) {
            for (GUIItem item : items.values()) {
                item.update(delta);
            }
        }
    }
}
