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

    /**
     * Number of Entities currently loaded.
     */
    public static int entityCount = 0;
    public static List<EntityUpdate> EntityList = new ArrayList<>();
    public static List<PhysicsEntity> PhysicsList = new ArrayList<>();
    
    /**
     * Generates a physics entity for the object given
     * 
     * @param p What object to be initialized
     * @param m Model to be bound to Entity
     * @return Error
     */
    
    public static boolean generatePhysicsEntity(PhysicsEntity p, Model m) {
        if(entityCount > 44)
        { p = null; return false;}
        entityCount++;
        p = new PhysicsEntity(m);
        PhysicsList.add(p);
        return EntityList.add(p);
    }

    /**
     * Generates a static entity for the object given
     * 
     * @param s What object to be initialized
     * @param m Model to be bound to Entity
     * @return Error
     */
    
    public static boolean generateStaticEntity(StaticEntity s, Model m) {
        if(entityCount > 44)
        { s = null; return false;}
        entityCount++;
        s = new StaticEntity(m);
        return EntityList.add(s);
    }
    
    /**
     * Destroys the entity given
     * 
     * @param en
     * @return Error
     */
    
    public static boolean destroyEntity(StaticEntity en) {
        entityCount--;
        if (!EntityList.remove(en)) {
            return false;
        }
        en = null;
        return true;
    }
    
    /**
     * Destroys the entity given
     * 
     * @param en
     * @return Error 
     */
    
    public static boolean destroyEntity(PhysicsEntity en) {
        if (!PhysicsList.remove(en)) {
            return false;
        }
        entityCount--;
        if (!EntityList.remove(en)) {
            return false;
        }
        en = null;
        return true;
    }
}
