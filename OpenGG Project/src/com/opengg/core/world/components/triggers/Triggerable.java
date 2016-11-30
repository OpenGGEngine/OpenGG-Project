/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.triggers;

import com.opengg.core.world.components.Component;

/**
 *
 * @author Javier
 */
public interface Triggerable extends Component{
    public void onTrigger(Trigger source, TriggerInfo info);
    public void onSubscribe(Trigger trigger);
}
