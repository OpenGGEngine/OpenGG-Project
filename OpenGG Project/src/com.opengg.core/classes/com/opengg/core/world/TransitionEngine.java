/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.world.components.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Javier
 */
public class TransitionEngine {
    static List<ComponentTransitionFrame> frames = new ArrayList<>();
    
    public static void add(Transition t){
        for(ComponentTransitionFrame frame : frames){
            if(frame.comp == t.getComponent()){
                frame.add(t);
                return;
            }
        }
        
        ComponentTransitionFrame frame = new ComponentTransitionFrame();
        frame.comp = t.getComponent();
        frame.add(t);
        frames.add(frame);
    }
    
    public static void update(float delta){
        for(Iterator<ComponentTransitionFrame> iterator = frames.iterator(); iterator.hasNext();){
            ComponentTransitionFrame frame = iterator.next();
            if(frame.update(delta)){
                iterator.remove();
            } 
        }
    }
    
    public static void remove(Component c){
        ComponentTransitionFrame ctf = null;
        for(ComponentTransitionFrame frame : frames){
            if(frame.comp == c)
                ctf = frame;
        }
        if(ctf != null)
            frames.remove(ctf);
    }
    
    public static void clearQueue(Component c){
        ComponentTransitionFrame ctf = null;
        for(ComponentTransitionFrame frame : frames){
            if(frame.comp == c)
                ctf = frame;
        }
        ctf.clearTransitions();
    }

    private TransitionEngine() {
    }
}
