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
    public List<ContactManifold> manifolds = new ArrayList<>();
    
    public static Collision reverse(Collision c){
        Collision c2 = new Collision();
        c2.other = c.thiscollider;
        c2.thiscollider = c.other;
        c2.manifolds.addAll(c.manifolds);
        return c2;
    }
    
    public int contains(ColliderGroup c){
        if(c == thiscollider)
            return 1;
        if(c == other)
            return 2;
        return 0;
    }
}
