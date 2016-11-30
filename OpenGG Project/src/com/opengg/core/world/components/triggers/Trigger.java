/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.triggers;

import com.opengg.core.world.components.*;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public abstract class Trigger implements Updatable{
    ArrayList<Triggerable> subscribers = new ArrayList<>();
    Component parent;
    boolean enabled;
    
    public void addSubscriber(Triggerable dest){
        subscribers.add(dest);
        dest.onSubscribe(this);
    }
    
    public boolean getTriggerState(){
        return enabled;
    }
    
    public ArrayList<Triggerable> getTriggerDest(){
        return subscribers;
    }
    
    public void forceTrigger(){
        for(Triggerable t : subscribers){
            t.onTrigger(this, null);
        }
    }

    @Override
    public void setParentInfo(Component parent) {
        this.parent = parent;
    }
}
