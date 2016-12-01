/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.physics;

/**
 *
 * @author Javier
 */
public class CollisionData{
    CollisionData(){};
    public boolean c1physact, c2physact;
    public PhysicsComponent c1phys, c2phys;  
    public Collider c1collider, c2collider;
    public BoundingBox c1colliderbox, c2colliderbox;
}
