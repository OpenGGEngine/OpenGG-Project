/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.triggers;

import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public interface ITrigger {
    
    public void addSubscriber(Triggerable dest); 
    public boolean getTriggerState();
    public ArrayList<Triggerable> getSubscribers();
    public void trigger(TriggerInfo ti);
}
