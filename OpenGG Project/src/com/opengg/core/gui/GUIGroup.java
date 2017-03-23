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
public class GUIGroup {
    
    public GUIGroup parent;
    
    public Vector2f pos;
    
    private Map<String,GUIItem> items = new HashMap<>();
    private Map<String, GUIGroup> groups = new HashMap<>();
    
    public GUIGroup(Vector2f pos){
        this.pos = pos;
    }

    public void addItem(String name, GUIItem item){
        item.parent = this;
        items.put(name, item);
    }
    
    public void addGroup(String name, GUIGroup group){
        group.parent = this;
        groups.put(name, group);
    }
    
    public void render(Vector2f local){
        
        for(GUIGroup group: groups.values()){
            group.render(new Vector2f(local.x + pos.x, local.y + pos.y));
        }
        for(GUIItem item: items.values()){
            item.render(new Vector2f(local.x + pos.x,local.y + pos.y));
        }
    }
}
