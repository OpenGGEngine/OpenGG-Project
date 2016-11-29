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
public interface Trigger extends Updatable{
    public void subscribeToTrigger(Triggerable dest);
    public ArrayList<Triggerable> getTriggerDest();
}
