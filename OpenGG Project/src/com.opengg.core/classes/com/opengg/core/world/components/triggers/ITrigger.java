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
    
    void addSubscriber(Triggerable dest);
    ArrayList<Triggerable> getSubscribers();
    void trigger(TriggerInfo ti);
}
