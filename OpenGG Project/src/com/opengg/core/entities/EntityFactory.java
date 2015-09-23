/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.entities;

import com.opengg.core.Model;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ethan
 */
    public abstract class EntityFactory {
        static public int entityCount = 0;
        public List<Entity> EntityList = new ArrayList<>();
        public List<PhysicsEntity> PhysicsList = new ArrayList<>();
        
    public boolean generatePhysicsEntity(PhysicsEntity p, Model m){
        entityCount++;
        p = new PhysicsEntity(m);
        PhysicsList.add(p);
        return EntityList.add(p);
    }
    
    public boolean generateStaticEntity(StaticEntity s, Model m)
    {
        entityCount++;
        s = new StaticEntity(m);
        return EntityList.add(s);
    }
    
    public boolean destroyEntity(Entity en)
    {
        if(en instanceof PhysicsEntity)
        {
            if(!PhysicsList.remove(en))
            {
                return false;
            }
        }
        entityCount--;
        if(!EntityList.remove(en))
        {
            return false;
        }
        en = null;
        return true;
    }
}
