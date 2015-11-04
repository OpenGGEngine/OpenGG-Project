/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.world.Camera;
import com.opengg.core.world.World;
import java.rmi.activation.ActivationException;

/**
 *
 * @author ethachu19
 */
public class PlayerEntity extends Entity{
    
    public Camera playerCam = new Camera(pos, direction);
    
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
     */
    public PlayerEntity(OBJModel model, World current) throws ActivationException{
        super(model, current);
    }

    /**
     * Creates an player based off of 5 parameters.
     *
     * @param f Force vector
     * @param mass Mass of Entity
     * @param type Type of entity
     * @param model Model to be bound to entity
     */
    public PlayerEntity(EntityType type, Vector3f position, Vector3f f, float mass, OBJModel model, World current) throws ActivationException{
        super(type,position, f, mass, model, current);
    }

    /**
     * Creates a new entity based off another.
     *
     * @param v Entity to be copied
     */
    public PlayerEntity(Entity v) throws ActivationException{
        super(v);
    }
}