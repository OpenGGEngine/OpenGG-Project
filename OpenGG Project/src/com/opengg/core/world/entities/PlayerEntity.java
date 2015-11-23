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

/**
 *
 * @author ethachu19
 */
public class PlayerEntity extends Entity{
    
    public Camera playerCam;

    /**
     * Makes default Player
     *
     */
    public PlayerEntity(){
        super();
        playerCam = new Camera(pos, direction);
        currentWorld.addCamera(playerCam);
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
    public PlayerEntity(EntityType type, Vector3f position, Vector3f f, float mass, OBJModel model, World current){
        super(type,position, f, mass, model, current);
        playerCam = new Camera(pos, direction);
        current.addCamera(playerCam);
    }

    /**
     * Creates a new entity based off another.
     *
     * @param v Entity to be copied
     */
    public PlayerEntity(Entity v){
        super(v);
        playerCam = new Camera(pos, direction);
        currentWorld.addCamera(playerCam);
    }
    
    @Override
    public void changeWorld(World next){
        currentWorld.removeCamera(playerCam);
        currentWorld = next;
        next.addCamera(playerCam);
    }
}