/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

import com.opengg.core.math.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.world.Camera;
import com.opengg.core.world.World;
import com.opengg.core.world.entities.resources.EntitySupportEnums.PhysicsType;

/**
 *
 * @author ethachu19
 */
public class PlayerEntity extends Entity{
    
    public Camera playerCam;
    public Vector3f direction = new Vector3f();

    /**
     * Makes default Player
     *
     */
    public PlayerEntity(){
        super();
    }

    /**
     * Creates an player based off of 5 parameters.
     *
     * @param f Force vector
     * @param position
     * @param mass Mass of Entity
     * @param type Type of entity
     * @param model Model to be bound to entity
     * @param current
     */
    public PlayerEntity(PhysicsType type, Vector3f position, Vector3f f, float mass, OBJModel model, World current){
        super(type,position, f, mass, model, current);
        playerCam = new Camera(this.current.pos, direction);
    }

    /**
     * Creates a new entity based off another.
     *
     * @param v Entity to be copied
     */
    public PlayerEntity(Entity v){
        super(v);
        playerCam = new Camera(current.pos, direction);
    }
    
    @Override
    public void changeWorld(World next){
        try{
            
        } catch (NullPointerException e) {}
        current.currentWorld = next;
    }
}