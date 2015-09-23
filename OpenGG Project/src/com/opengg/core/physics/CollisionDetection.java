/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.entities.EntityFactory;

/**
 *
 * @author ethachu19
 */
public class CollisionDetection extends EntityFactory{
    
    public int areColliding(int x, int y)
    {
        //Possible error checking
        if(PhysicsList.get(x) == null || EntityList.get(y) == null)
        {
            return -1;
        }
        
        
        
        return 0;
    }
    
}
