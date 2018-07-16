/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.world.Camera;

/**
 *
 * @author Javier
 */
public class GUI{
    private GUIGroup root = new GUIGroup(new Vector2f(0,0));
    private boolean menu = false;
    
    public GUI() {}

    public void render(){
        root.render();
    }
    
    public void addItem(String name, GUIRenderable item){
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
}
