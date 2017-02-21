/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import com.opengg.core.world.components.triggers.Triggerable;
import java.util.List;

/**
 *
 * @author Ethan Mak
 */
public class TriggerableCollisionResponse implements Triggerable{

    @Override
    public void onTrigger(Trigger source, TriggerInfo info) {
        if (!(source instanceof Collider))
            throw new IllegalStateException("Source of Response not Collider");
        if (!info.info.equals("collision"))
            return;
        Collider parent = (Collider) source;
        List<CollisionData> collisions = (List<CollisionData>) info.data;
        
    }

    @Override
    public void onSubscribe(Trigger trigger) {}
    
    public void getAngleOfCollision() {
        
    }
    
}
