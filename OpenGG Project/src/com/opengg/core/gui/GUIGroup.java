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
    
    public boolean active = true;
    GUIGroup parent;
    
    Vector2f pos;
    
    Map<String,VisualGUIItem> items = new HashMap<>();
    Map<String, GUIGroup> groups = new HashMap<>();
    
    public GUIGroup(Vector2f pos){
        this.pos = pos;
    }

    public void addItem(String name, VisualGUIItem item){
        item.setParent(this);
        items.put(name, item);
    }
    
    public void addGroup(String name, GUIGroup group){
        group.setParent(this);
        groups.put(name, group);
    }
    
    public VisualGUIItem getItem(String name){
        return items.get(name);
    }
    
    public GUIGroup getGroup(String name){
        return groups.get(name);
    }
    
    public void setParent(GUIGroup group){
        this.parent = group;
    }
    
    public void render(){
        if(active){
            for(GUIGroup group: groups.values()){
                group.render();
            }
        
            for(VisualGUIItem item: items.values()){
                item.render();
            }
        }
    }
    
    public Vector2f getPosition(){
        if(parent == null)
            return pos;
        
        return pos.add(parent.getPosition());
    }
}
