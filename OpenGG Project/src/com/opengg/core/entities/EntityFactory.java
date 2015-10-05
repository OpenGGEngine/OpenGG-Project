/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.entities;

import com.opengg.core.Vector3f;
import com.opengg.core.entities.Entity.EntityType;
import com.opengg.core.objloader.parser.OBJModel;
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
     * List of currently loaded entities
     */
    public static final List<Entity> EntityList = new ArrayList<>();
    
    /**
     * Generates a entity with the parameters given
     * 
     * @param tag What type of entity
     * @param m Model to be bound to Entity
     * @return Error
     */
    public static Entity generateEntity(EntityType tag, OBJModel m) {
        if(entityCount > 44)
        {return null;}
        entityCount++;
        return new Entity(m, tag);
    }
    
    public static Entity generateEntity(EntityType tag, float x, float y, float z, Vector3f f, float mass, float volume) {
        if(entityCount > 44)
        {return null;}
        entityCount++;
        return new Entity(x,y,z,f,mass,volume,tag);
    }
    
    public static Entity generateEntity(Entity v) {
        if(entityCount > 44)
        {return null;}
        entityCount++;
        return new Entity(v);
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
