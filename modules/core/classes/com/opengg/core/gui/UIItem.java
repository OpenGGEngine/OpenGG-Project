/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;

/**
 *
 * @author Javier
 */
public abstract class UIItem {
    public boolean enabled = true;
    private float layer = 0f;
    private String name = "";
    private Vector2f position = new Vector2f();
    private UIGroup parent;

    public float getLayer() {
        if(parent == null) return layer;
        return layer + parent.getLayer();
    }

    public UIItem setLayer(float layer) {
        this.layer = layer;
        return this;
    }  

    public boolean isEnabled() {
        return parent != null && enabled && parent.isEnabled();
    }

    public boolean isLocallyEnabled(){
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) onEnable();
        if(!enabled) onDisable();
    }

    public void onEnable(){

    }

    public void onDisable(){

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

    public UIItem setPositionOffset(Vector2f position) {
        this.position = position;
        return this;
    }
    
    public Vector2f getPositionOffset(){
        return position;
    }

    public void setParent(UIGroup parent){
        this.parent = parent;

        if(parent != null && enabled){
            this.onEnable();
        }

        if(parent == null){
            this.onDisable();
        }
    }

    public abstract void render();

    public void update(float delta){

    }
}
