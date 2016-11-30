/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import com.opengg.core.util.Time;
import com.opengg.core.world.components.Updatable;
import java.util.ArrayList;

/**
 *
 * @author Javier Coindreau 
 */
public class UpdateEngine{
    
    static ArrayList<Updatable> objs = new ArrayList<>();
    static Time t;
    
    static{
        t = new Time();
    }
    
    public static void addObjects(Updatable e){
        objs.add(e);
    }
    
    public static void update(){
        float i = t.getDeltaSec();
        for(Updatable e : objs){
            e.update(i);
        }
    }
    
}
