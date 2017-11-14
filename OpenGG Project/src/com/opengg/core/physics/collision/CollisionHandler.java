/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.physics.PhysicsSystem;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class CollisionHandler {
    static List<Collision> collisions = new ArrayList<>();
    static List<ColliderGroup> test = new LinkedList<>();
    
    public static void clearCollisions(){
        collisions.clear();
    }
    
    public static void testForCollisions(PhysicsSystem system){
        collisions.clear();
        for(ColliderGroup next : test){
            for(ColliderGroup other : system.getColliders()){
                Collision col = null;
                if(next == other) continue;
                for(Collision c : collisions){
                    if(c.contains(next) > 0 && c.contains(other) > 0){
                        continue;
                    }
                }
                
                if(col == null){
                    collisions.add(next.testForCollision(other));
                }
            }
        }
    }
}
