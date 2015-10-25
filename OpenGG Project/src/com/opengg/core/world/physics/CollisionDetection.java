/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.physics;

import com.opengg.core.world.entities.Entity;
import com.opengg.core.world.entities.EntityFactory;

/**
 *
 * @author ethachu19
 */
public class CollisionDetection extends EntityFactory {
    private static final short MAX = 1;
    private static final short MIN = 0;
    
    public static boolean areColliding(Entity collide, Entity collidee) {

        //Possible error checking
        if (collide == null || collidee == null) {
            return false;
        }
        
         if (collide.boundingBox[MAX].x < collidee.boundingBox[MIN].x || 
             collide.boundingBox[MAX].y < collidee.boundingBox[MIN].y ||
             collide.boundingBox[MAX].z < collidee.boundingBox[MIN].z ||
             collide.boundingBox[MIN].x > collidee.boundingBox[MAX].x || 
             collide.boundingBox[MIN].y > collidee.boundingBox[MAX].y ||
             collide.boundingBox[MIN].z > collidee.boundingBox[MAX].z) 
            {
                return false;
            }
        
        
         
         
        return true;
    }

}
