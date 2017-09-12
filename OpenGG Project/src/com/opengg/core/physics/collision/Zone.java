/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.physics.CollisionComponent;

/**
 *
 * @author ethachu19
 */
public class Zone extends Component{
    CollisionComponent collider;
    Vector3f pos;
    
    public Zone(AABB box) {
        pos = box.pos;
        collider = new CollisionComponent(box);
        this.attach(collider);
    }
    
    
}
