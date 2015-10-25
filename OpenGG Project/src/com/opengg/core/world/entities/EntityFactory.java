/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

import com.opengg.core.Vector3f;
import com.opengg.core.world.entities.EntityEnums.EntityType;
import com.opengg.core.io.objloader.parser.OBJModel;
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
     * @return New Entity
     */
    public static Entity generateEntity(EntityType tag, OBJModel m) {
        if(entityCount > 44)
        {return null;}
        entityCount++;
        return new Entity(m, tag);
    }
    
    /**
     * Generates a entity with the parameters given
     * 
     * @param tag EntityType
     * @param x X Coordinate of Entity
     * @param y Y Coordinate of Entity
     * @param z Z Coordinate of Entity
     * @param f f Force Vector
     * @param mass Mass of Entity
     * @param volume Volume of Entity
     * @return New Entity
     */
    public static Entity generateEntity(EntityType tag, float x, float y, float z, Vector3f f, float mass, float volume, OBJModel model) {
        if(entityCount > 44)
        {return null;}
        entityCount++;
        return new Entity(x,y,z,f,mass,volume,tag,model);
    }
    
    /**
     * Generates a entity with the parameters given
     * 
     * @param v Entity to be copied
     * @return New Entity
     */
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
        en.forceCalculator = null;
        en = null;
        return true;
    }
}
