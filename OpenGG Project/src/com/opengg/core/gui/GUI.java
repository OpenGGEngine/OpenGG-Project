/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.gui;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import com.opengg.core.render.shader.Mode;
import com.opengg.core.util.GlobalInfo;
import com.opengg.core.world.Camera;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class GUI {
    Vector2f low,high;
    private ArrayList<GUIItem> guiitems = new ArrayList<>();
    
    public GUI(Vector2f low, Vector2f high) {
        this.low = low;
        this.high = high;
    }
    
    public void setupGUI(Vector2f lowBound, Vector2f highBound){
        low = lowBound;
        high = highBound;
    }
    public static void startGUIPos(){
        GlobalInfo.main.setOrtho(-1, 1, -1, 1, -1, 1);
        GlobalInfo.main.setView(new Camera());
    }
    public static void enableGUI(){
        GlobalInfo.main.setMode(Mode.GUI);
    }
    public void addItem(GUIItem item){
        guiitems.add(item);
    }
    public void render(){
        for(GUIItem item: guiitems){ 
            //GlobalInfo.main.setModel(Matrix4f.translate(item.screenlocalpos.x + low.x, item.screenlocalpos.y + low.y, 0));
            GlobalInfo.main.setModel(Matrix4f.translate(new Vector3f(0,0,0)));
            item.render();
        }
    }
}
