/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.physics.collision;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class CollisionData{
    CollisionData(){};
    public CollisionComponent collider1, collider2;
    public Vector3f collisionNormal;
    public Vector3f collisionPoint;
    
}
