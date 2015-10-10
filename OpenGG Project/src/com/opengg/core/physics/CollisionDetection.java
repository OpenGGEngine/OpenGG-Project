/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.entities.Entity;
import com.opengg.core.entities.EntityFactory;

/**
 *
 * @author ethachu19
 */
public class CollisionDetection extends EntityFactory{
    
    public static int areColliding(Entity collide, Entity collidee)
    {
        //Possible error checking
        if(collide == null || collidee == null)
        {
            return -1;
        }
        
         if (collide.boundingBox[4].x < collidee.boundingBox[6].x || 
             collide.boundingBox[4].y < collidee.boundingBox[6].y || 
             collide.boundingBox[6].x > collidee.boundingBox[4].x || 
             collide.boundingBox[6].y > collidee.boundingBox[4].y) 
            {
                return 0;
            }
        
        
         
         
        return 1;
    }
    
}
