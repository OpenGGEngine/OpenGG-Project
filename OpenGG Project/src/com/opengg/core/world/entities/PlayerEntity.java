/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.world.Camera;
import com.opengg.core.world.entities.EntityEnums.EntityType;

/**
 *
 * @author ethachu19
 */
public class PlayerEntity extends Entity{
    
    public Camera playerCam = new Camera(new Vector3f(), new Vector3f());
    
    /**
     * Default Constructor
     */
    public PlayerEntity() {
        super();
    }

    /**
     * Makes default Player
     *
     * @param model Model to be bound to Entity
     * @param type Type of Entity
     * @param heightofGround Height of Ground
     */
    public PlayerEntity(OBJModel model, EntityType type, float heightofGround){
        super(model, type, heightofGround);
    }

    /**
     * Creates an player based off of 5 parameters.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param heightofGround
     * @param f Force vector
     * @param mass Mass of Entity
     * @param volume Volume of Entity
     * @param type Type of entity
     * @param model Model to be bound to entity
     */
    public PlayerEntity(float x, float y, float z, float heightofGround, Vector3f f, float mass, float volume, EntityType type, OBJModel model){
        super(x, y, z, heightofGround, f, mass, volume, type, model);
    }

    /**
     * Creates a new entity based off another.
     *
     * @param v Entity to be copied
     */
    public PlayerEntity(Entity v){
        super(v);
    }
}