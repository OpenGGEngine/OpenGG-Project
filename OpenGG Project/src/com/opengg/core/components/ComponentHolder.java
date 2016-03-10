/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.components;

import java.util.ArrayList;

/**
 *
 * @author ethachu19
 */
public abstract class ComponentHolder {
    //public static ArrayList<ComponentHolder> allComponent = new ArrayList<>();
    private ArrayList<Updatable> updateable = new ArrayList<>();
    private ArrayList<Renderable> renderable = new ArrayList<>();
    
    public void attach(Component c) {
        if (c instanceof Updatable)
                updateable.add((Updatable) c);
        if (c instanceof Renderable)
                renderable.add((Renderable) c);
    }
    
    public void update(float delta){
        for(Updatable c: updateable){
            c.update(delta);
        }
    }
    
    public void render(){
        for(Renderable c: renderable){
            c.render();
        }
    }
    
                                                                                                                                                                           
}
