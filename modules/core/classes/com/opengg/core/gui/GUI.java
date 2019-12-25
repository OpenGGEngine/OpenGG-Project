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
public class GUI{
    private GUIGroup root = new GUIGroup(new Vector2f(0,0));
    private boolean menu = false;
    private String name = "";
    
    public GUI() {}

    public void render(){
        root.render();
    }

    public void update(float delta){
        root.update(delta);
    }
    
    public void addItem(String name, GUIItem item){
        root.addItem(name, item);
    }

    public GUIGroup getRoot() {
        return root;
    }

    public boolean isMenu() {
        return menu;
    }

    public void setMenu(boolean menu) {
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
