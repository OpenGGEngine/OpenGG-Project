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
    public static ArrayList<ComponentHolder> allComponent = new ArrayList<>();
    private ArrayList<Component> components = new ArrayList<>();
    
    public ComponentHolder() {
        allComponent.add(this);
    }
    
    public void add(Component c) {
        components.add(c);
    }
    
    public ArrayList<Component> getComponents() {
        return components;
    }
    
    public void runAll(float delta) {
        for (Component c: components) {
            if (c instanceof Updatable)
                ((Updatable) c).update(delta);
            if (c instanceof Renderable)
                ((Renderable) c).render();
        }
    }
    
                                                                                                                                                                           
}
