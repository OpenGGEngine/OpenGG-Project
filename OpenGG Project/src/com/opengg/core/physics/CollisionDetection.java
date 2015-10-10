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
        
         if (collide.boundingBox[1].x < collidee.boundingBox[0].x || 
             collide.boundingBox[1].y < collidee.boundingBox[0].y || 
             collide.boundingBox[0].x > collidee.boundingBox[1].x || 
             collide.boundingBox[0].y > collidee.boundingBox[1].y) 
            {
                return 0;
            }
        
        
         
         
        return 1;
    }
    
}
