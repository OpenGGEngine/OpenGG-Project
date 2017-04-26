/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.collision;

import com.opengg.core.world.components.physics.CollisionComponent;
import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public abstract class Collider {
    CollisionComponent parent;
    Vector3f offset = new Vector3f();
    public abstract Collision isColliding(Collider c);
    
    public Vector3f getPosition(){
        return offset.add(parent.getPosition());
    }
    
    public void setParent(CollisionComponent parent){
        this.parent = parent;
    }
}
