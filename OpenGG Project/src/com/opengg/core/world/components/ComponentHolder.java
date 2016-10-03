/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import java.util.ArrayList;

/**
 *
 * @author ethachu19
 */
public abstract class ComponentHolder implements Component{
    //public static ArrayList<ComponentHolder> allComponent = new ArrayList<>();
    protected ArrayList<Updatable> updateable = new ArrayList<>();
    protected ArrayList<Renderable> renderable = new ArrayList<>();
    protected ArrayList<Triggerable> triggerable = new ArrayList<>();
    
    public void attach(Component c) {
        c.setParentInfo(this);
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
    
    public ArrayList<Updatable> getUpdatables(){
        return updateable;
    }
    
    public ArrayList<Renderable> getRenderables(){
        return renderable;
    }
     
    public ArrayList<Triggerable> getTriggerables(){
        return triggerable;
    }
}
