/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.engine.WorldEngine;
import java.util.ArrayList;

/**
 *
 * @author ethachu19
 */
public abstract class ComponentHolder extends Component{
    protected ArrayList<Component> children = new ArrayList<>();
    
    public void attach(Component c) {
        c.setParentInfo(this);
        WorldEngine.addObjects(c);
        children.add(c);
    }  
    
    public ArrayList<Component> getChildren(){
        return children;
    }
    
    public void remove(int i){
        children.remove(i);
    }
    
    public void remove(Component w){
        children.remove(w);
    }
}
