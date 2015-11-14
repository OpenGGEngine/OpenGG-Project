/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.world.World;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ethachu19
 */
public class EntityFactory {
    
    /**
     * Number of Entities currently loaded.
     */
    public static int entityCount = 0;
    
    /**
     * List of currently loaded entities
     */
    public static final List<Entity> EntityList = new ArrayList<>();
    
    /**
     * Amount of Entities
     */
    public static int entityCap = 44;
    /**
     * Generates an entity with parameters given
     * 
     * @param et Type of Entity
     * @param e Entity to be copied
     * @return new Entity 
     */
    public static final Entity getEntity(EntityTypes et, Entity e){
        if(entityCount >= entityCap)
            return null;
        ++entityCount;
        switch(et){
            case PLAYER:
                return new PlayerEntity(e);
            default:
                return new Entity(e);
        }
    }
    
    /**
     * Generates an entity with parameters given
     * 
     * @param et Type of Entity
     * @param type Type of Entity(For Tags)
     * @param pos Position of Entity
     * @param f Force Vector
     * @param mass Mass of Entity
     * @param model Model to be bound to Entity
     * @param thisWorld CurrentWorld of Entity
     * @return new Entity
     */
    public static final Entity getEntity(EntityTypes et, Entity.EntityType type,Vector3f pos, Vector3f f, float mass,OBJModel model, World thisWorld){
        if(entityCount >= entityCap)
            return null;
        ++entityCount;
        switch(et){
            case PLAYER:
                return new PlayerEntity(type,pos,f,mass,model,thisWorld);
            default:
                return new Entity(type,pos,f,mass,model,thisWorld);
        }
    }
    
    /**
     * Generates an entity with the parameters given
     * 
     * @param type Type of Entity
     * @param world Current World
     * @return new Entity
     */
    public static final Entity getEntity(EntityTypes type, World world){
        if(entityCount >= entityCap)
            return null;
        ++entityCount;
        switch(type){
            case PLAYER:
                return new PlayerEntity(new OBJModel(), world);
            default:
                return new Entity(new OBJModel(), world);
        }
    }
    
    /**
     * Destroys entity given
     * 
     * @param des Entity to be destroyed
     * @return Error
     */
    public static final boolean destroyEntity(Entity des){
        des = null;
        return EntityList.remove(des);
    }
    
    /**
     * Destroys entity given
     * 
     * @param i Index of entity to be destroyed
     * @return Error
     */
    public static final boolean destroyEntity(int i){
        Entity des = EntityList.get(i);
        des = null;
        return EntityList.remove(des);
    }
}
