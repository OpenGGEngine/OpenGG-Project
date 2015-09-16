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

    public float x;
    public float y;
    public float z;
    public float velocity;
    public float acceleration;

    /**
     * Creates a default entity with all values set to 0.
     */
    public Entity() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
        this.velocity = 0f;
        this.acceleration = 0f;
    }

    /**
     * Creates an entity based off of 5 parameters.
     *
     * @param x
     * @param y
     * @param z
     * @param velocity
     * @param acceleration
     */
    public Entity(float x, float y, float z, float velocity, float acceleration) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    /**
     * Creates a new vector based off another.
     *
     * @param v Entity to be copied
     */
    public Entity(Entity v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.velocity = v.velocity;
        this.acceleration = v.acceleration;
    }

    /**
     * Creates new Entity off of old entity with different movement speed
     * 
     * @param velocity
     * @param acceleration
     * @param v Entity to be copied
     */
    public Entity(float velocity, float acceleration, Entity v) {
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
}
