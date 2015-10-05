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
    
    public static int areColliding(Entity x, Entity y)
    {
        //Possible error checking
        if(x == null || y == null)
        {
            return -1;
        }
        
        
        
        return 0;
    }
    
}
