/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.entities;

import com.opengg.core.Model;
import com.opengg.core.Vector3f;
import com.opengg.core.entities.Entity.EntityType;
import com.opengg.core.physics.ForceManipulation;
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
    /**
     * List of currently loaded enemies
     */
    public static List<Entity> EntityList = new ArrayList<>();
    
    /**
     * Generates a physics entity for the object given
     * 
     * @param p What object to be initialized
     * @param tag What type of entity
     * @param m Model to be bound to Entity
     * @return Error
     */
    public static boolean generateEntity(Entity p, EntityType tag, Model m) {
        if(entityCount > 44)
        { p = null; return false;}
        entityCount++;
        p = new Entity(m, tag);
        p.forceCalculator = new ForceManipulation(p);
        return EntityList.add(p);
    }
    
    public static boolean generateEntity(Entity p, EntityType tag, float x, float y, float z, Vector3f f, float mass, float volume) {
        if(entityCount > 44)
        { p = null; return false;}
        entityCount++;
        
            p = new Entity(x,y,z,f,mass,volume,tag);
        
        
            p.forceCalculator = new ForceManipulation(p);
        
        return EntityList.add(p);
    }
    
    public static boolean copyEntity(Entity p, Entity v) {
        if(entityCount > 44)
        { p = null; return false;}
        entityCount++;
        p = new Entity(v);
        p.forceCalculator = new ForceManipulation(v.forceCalculator.airResistance, v.forceCalculator.force, p);
        
        return EntityList.add(p);
    }
    
    /**
     * Destroys the entity given
     * 
     * @param en
     * @return Error
     */
    public static boolean destroyEntity(Entity en) {
        entityCount--;
        if (!EntityList.remove(en)) {
            return false;
        }
        en = null;
        return true;
    }
}
