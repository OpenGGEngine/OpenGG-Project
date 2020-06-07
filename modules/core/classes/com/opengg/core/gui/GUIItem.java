/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector2i;

/**
 *
 * @author Javier
 */
public abstract class GUIItem {
    public boolean enabled = true;
    String name = "";
    Vector2f position = new Vector2f();
    float layer = 0f;
    GUIGroup parent; 

    public float getLayer() {
        return layer;
    }

    public GUIItem setLayer(float layer) {
        this.layer = layer;
        return this;
    }  

    public boolean isEnabled() {
        if(parent == null)
            return enabled && GUIController.isEnabled();
        else
            return enabled && parent.isEnabled() && GUIController.isEnabled();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector2f getSize(){
        return new Vector2f();
    }

    public Vector2f getPosition() {
        if(parent == null) {
            return position;
        }
        return position.add(parent.getPosition());
    }

    public GUIItem setPositionOffset(Vector2f position) {
        this.position = position;
        return this;
    }
    
    public Vector2f getPositionOffset(){
        return position;
    }

    public void setParent(GUIGroup parent){
        this.parent = parent;
    }

    public abstract void render();

    public void update(float delta){

    }
}
