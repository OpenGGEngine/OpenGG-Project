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
public class Trigger extends Component implements ITrigger{
    ArrayList<Triggerable> subscribers = new ArrayList<>();
    
    @Override
    public void addSubscriber(Triggerable dest){
        subscribers.add(dest);
        dest.onSubscribe(this);
    }
    
    @Override
    public void trigger(TriggerInfo ti){
        for(Triggerable t : subscribers){
            t.onTrigger(this, ti);
        }
    }

    @Override
    public ArrayList<Triggerable> getSubscribers() {
        return subscribers;
    }
}
