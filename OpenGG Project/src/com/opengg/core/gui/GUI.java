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
    public static void startGUI(Camera c){
        //GlobalInfo.main.setOrtho(low.x, high.x, low.y, high.y, 0.2f, 10);      
        c.setPos(new Vector3f(0,0,0));
        c.setRot(new Vector3f(0,0,0));
        GlobalInfo.main.setView(c);
        System.out.println(c.getPos());
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
