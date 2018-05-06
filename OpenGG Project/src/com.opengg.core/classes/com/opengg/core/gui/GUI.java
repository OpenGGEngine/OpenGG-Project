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
    public static GUIGroup root = new GUIGroup(new Vector2f(0,0));
    
    public GUI() {
    }
    
    public static void startGUIPos(){
        ShaderController.setOrtho(-1, 1, -1, 1, -1f, 1f);
        ShaderController.setView(new Camera().getMatrix());
    }

    public void render(){
        root.render();
    }
    
    public void addItem(String name, GUIRenderable item){
        root.addItem(name, item);
    } 
}
