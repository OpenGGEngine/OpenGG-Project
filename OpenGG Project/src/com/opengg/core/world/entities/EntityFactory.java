/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.world.World;
import java.rmi.activation.ActivationException;

/**
 *
 * @author ethachu19
 */
public class EntityFactory {
    public static final Entity getEntity(EntityTypes et, Entity e) throws ActivationException{
        switch(et){
            case PLAYER:
                return new PlayerEntity(e);
            default:
                return new Entity(e);
        }
    }
    
    public static final Entity getEntity(EntityTypes type, Entity.EntityType et,Vector3f pos, Vector3f f, float mass,OBJModel model, World thisWorld) throws ActivationException{
        switch(type){
            case PLAYER:
                return new PlayerEntity(et,pos,f,mass,model,thisWorld);
            default:
                return new Entity(et,pos,f,mass,model,thisWorld);
        }
    }
    
    public static final Entity getEntity(EntityTypes type, World world) throws ActivationException{
        switch(type){
            case PLAYER:
                return new PlayerEntity(new OBJModel(), world);
            default:
                return new Entity(new OBJModel(), world);
        }
    }
}
