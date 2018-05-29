/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.world.components.Component;

/**
 *
 * @author Javier
 */
public abstract class Transition {
    protected Component comp;
    protected float duration;
    protected float elapsed;
    protected boolean running = true;
    
    
    public float duration(){
        return duration;
    }
    
    public float elapsedTime(){
        return elapsed;
    }
    
    public Component getComponent(){
        return comp;
    }
    
    public void stop(){}
    
    public final boolean updateInitial(float delta){
        if(!running)
            return false;
        elapsed += delta;
        if(elapsed > duration)
            return true;
        update(delta);
        return false;
    }
    
    public abstract void update(float delta);
}
