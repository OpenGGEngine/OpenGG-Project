/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.entities.Entity;
import com.opengg.core.entities.PhysicsEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ethachu19
 */
public class CollisionDetection {
    List<PhysicsEntity> list = new ArrayList<>();
    
    public void addEntity(PhysicsEntity entity)
    {
        list.add(entity);
    }
    
    public void checkforNulls()
    {
        for(int i = 0; i<list.size(); i++)
        {
            if(list.get(i) == null)
            {
                list.remove(i);
            }
        }
    }
    
    public boolean areColliding(int x, int y)
    {
        if(list.get(x) == null || list.get(y) == null)
        {
            return false;
        }
        
        
        
        return true;
    }
    
}
