/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.world.components.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ComponentTransitionFrame {
    Component comp;
    HashMap<String, List<Transition>> types = new HashMap<>();
    
    public void add(Transition transition){
        String name = transition.getClass().getCanonicalName();
        if(!types.containsKey(name)){
            types.put(name, new ArrayList<>());
            types.get(name).add(transition);
            return;
        }
        types.get(name).add(transition);
    }
    
    public boolean update(float delta){
        for(Iterator<List<Transition>> lists = types.values().iterator(); lists.hasNext();){
            List<Transition> list = lists.next();
            list.removeIf(t -> t.updateInitial(delta));
            if(list.isEmpty())
                lists.remove();
        }
        return types.isEmpty();
    }
    
    public void clearTransitions(){
        types.clear();
    }
}
