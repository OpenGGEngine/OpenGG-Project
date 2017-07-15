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
public class Collision{
    Collision(){};
    public CollisionComponent thiscollider, other;
    public Vector3f collisionNormal;
    public Vector3f collisionPoint;
    public Vector3f overshoot;
    
    public static Collision reverse(Collision c){
        Collision c2 = new Collision();
        c2.other = c.thiscollider;
        c2.thiscollider = c.other;
        c2.collisionNormal = c.collisionNormal;
        c2.collisionPoint = c.collisionPoint;
        c2.overshoot = c.overshoot.inverse();
        return c2;
    }
    
    public int contains(CollisionComponent c){
        if(c == thiscollider)
            return 1;
        if(c == other)
            return 2;
        return 0;
    }
}
