/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.entities;

import com.opengg.core.Model;
import com.opengg.core.Vector3f;

/**
 *
 * @author ethachu19
 */
public class Entity {
    
    static public int entityCount = 0;
    public Vector3f pos = new Vector3f();
    public float velocity = 0f;
    
    public float acceleration;
    public boolean ground;
    public float mass;
    public float volume;

    
    public Entity(Model model) {
        
        pos.x = 0f;
        pos.y = 0f;
        pos.z = 0f;
        this.velocity = 0f;
        this.acceleration = 0f;
        this.ground = true;
        this.volume = 0f;
        this.mass = 0f;
        Entity.entityCount++;
    }

    /**
     * Creates an entity based off of 5 parameters.
     *
     * @param x
     * @param y
     * @param z
     * @param velocity
     * @param force
     * @param ground
     * @param mass
     * @param volume
     */
    public Entity(float x, float y, float z, float force, boolean ground, float mass, float volume) {
        
        pos.x = x;
        pos.y = y;
        pos.z = z;
        this.acceleration = force/mass;
        this.ground = ground;
        this.volume = volume;
        this.mass = mass;
        Entity.entityCount++;
    }

    /**
     * Creates a new vector based off another.
     *
     * @param v Entity to be copied  
     */
    public Entity(Entity v){
        
        pos.x = v.pos.x;
        pos.y = v.pos.y;
        pos.z = v.pos.z;
        this.velocity = v.velocity;
        this.acceleration = v.acceleration;
        this.ground = v.ground;
        this.volume = v.volume;
        this.mass = v.mass;
        Entity.entityCount++;
    }

    /**
     * Creates new Entity off of old entity with different movement speed
     * 
     * @param force
     * @param v Entity to be copied
     */
    public Entity(float force, Entity v) {
        
        this.acceleration = force/v.mass;
        pos.x = v.pos.x;
        pos.y = v.pos.y;
        pos.z = v.pos.z;
        this.ground = v.ground;
        this.volume = v.volume;
        this.mass = v.mass;
        Entity.entityCount++;
    }
}
