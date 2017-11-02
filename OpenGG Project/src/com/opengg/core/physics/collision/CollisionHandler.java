/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.engine.PhysicsEngine;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.world.components.physics.CollisionComponent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class CollisionHandler {
    static List<Collision> collisions = new ArrayList<>();
    
    public static void clearCollisions(){
        collisions.clear();
    }
    
    public static List<Collision> testForCollisions(ColliderGroup collider){
        List<Collision> ncollisions = new ArrayList<>();
        for(Collision c : collisions)
            if(c.contains(collider) == 1)
                ncollisions.add(c);
            else if(c.contains(collider) == 2)
                ncollisions.add(Collision.reverse(c));
        
        
        traverse: for(ColliderGroup comp : PhysicsEngine.getInstance().getColliders()){
            if(comp == collider)
                continue;
            for(Collision c : ncollisions)
                if(c.contains(comp) != 0)
                    continue traverse;
      
            List<Collision> info = collider.testForCollision(comp);
            if(!(info == null || info.isEmpty())){
                ncollisions.addAll(info);
                collisions.addAll(info);
            }
        }
        
        return ncollisions;
    }
}
