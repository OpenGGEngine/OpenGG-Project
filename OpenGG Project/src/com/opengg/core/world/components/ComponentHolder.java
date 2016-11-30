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
    protected ArrayList<Component> children = new ArrayList<>();
    
    public void attach(Component c) {
        c.setParentInfo(this);
        children.add(c);
    }  
    
    public ArrayList<Component> getChildren(){
        return children;
    }
}
