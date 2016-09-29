/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import com.opengg.core.util.GlobalInfo;
import com.opengg.core.util.Time;
import com.opengg.core.world.WorldObject;
import java.util.ArrayList;

/**
 *
 * @author Javier Coindreau 
 */
public class WorldEngine{
    
    ArrayList<WorldObject> objs = new ArrayList<>();
    Time t;
    
    public WorldEngine(){
        t = new Time();
        GlobalInfo.engine = this;
    }
    
    public void addObjects(WorldObject e){
        objs.add(e);
    }
    
    public void update(){
        
        float i = t.getDeltaSec();
        //System.out.println("Demo Disk");
        for(WorldObject e : objs){
            e.update(i);
        }
    }
    
}
