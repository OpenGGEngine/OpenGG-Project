/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.entities;

/**
 *
 * @author ethachu19
 */
public class Entity {
    
    static public int entityCount = 0;
    public float x;
    public float y;
    public float z;
    public float velocity = 0f;
    public float acceleration;
    public boolean ground;
    public float mass;
    public float volume;

    /**
     * Creates a default entity with all values set to 0 and on ground.
     * @throws java.lang.IllegalAccessException
     */
    public Entity() throws IllegalAccessException {
        if(entityCount >= 40)
        {
            throw new IllegalAccessException("Entity count is too high");
        }
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
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
     * @throws java.lang.IllegalAccessException
     */
    public Entity(float x, float y, float z, float force, boolean ground, float mass, float volume) throws IllegalAccessException {
        if(entityCount >= 40)
        {
            throw new IllegalAccessException("Entity count is too high");
        }
        this.x = x;
        this.y = y;
        this.z = z;
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
     * @throws java.lang.IllegalAccessException
     */
    public Entity(Entity v) throws IllegalAccessException {
        if(entityCount >= 40)
        {
            throw new IllegalAccessException("Entity count is too high");
        }
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
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
     * @throws java.lang.IllegalAccessException
     */
    public Entity(float force, Entity v) throws IllegalAccessException {
        if(entityCount >= 40)
        {
            throw new IllegalAccessException("Entity count is too high");
        }
        this.acceleration = force/v.mass;
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.ground = v.ground;
        this.volume = v.volume;
        this.mass = v.mass;
        Entity.entityCount++;
    }
}
