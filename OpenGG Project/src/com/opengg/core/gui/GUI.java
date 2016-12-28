/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.gui;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.shader.Mode;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.world.Camera;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class GUI {
    private static ArrayList<GUIItem> guiitems = new ArrayList<>();

    public static void startGUIPos(){
        ShaderController.setOrtho(-1, 1, -1, 1, -1, 1);
        ShaderController.setView(new Camera());
    }
    public static void enableGUI(){
        ShaderController.setMode(Mode.GUI);
    }
    public static void addItem(GUIItem item){
        guiitems.add(item);
    }
    public static void render(){
        for(GUIItem item: guiitems){ 
            ShaderController.setModel(Matrix4f.translate(item.screenlocalpos.x, item.screenlocalpos.y, 0));
            item.render();
        }
    }
}
