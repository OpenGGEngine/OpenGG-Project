/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Collision{
    Collision(){}

    public ColliderGroup thiscollider, other;
    public ContactManifold manifold;

    public Collision(ColliderGroup thiscollider, ColliderGroup other, ContactManifold manifold) {
        this.thiscollider = thiscollider;
        this.other = other;
        this.manifold = manifold;
    }

    public static Collision reverse(Collision c){
        Collision c2 = new Collision();
        c2.other = c.thiscollider;
        c2.thiscollider = c.other;
        c2.manifold = c.manifold.reverse();
        return c2;
    }
    
    public boolean contains(ColliderGroup c){
        return (c == thiscollider || c == other);
    }
}
