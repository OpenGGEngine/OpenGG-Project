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
    
    public Vector3f pos = new Vector3f();
    public float volume;
    public boolean ground;
    public float mass;
    public Vector3f force = new Vector3f();
    public Vector3f velocity = new Vector3f();
    public Vector3f direction = new Vector3f();
    
    
    public Entity()
    {
        
    }
    
    /**
     * Makes default Entity
     * 
     * @param model Model to be bound to Entity
     */
    
    public Entity(Model model) {
        
        pos.x = 0f;
        pos.y = 0f;
        pos.z = 0f;
        this.ground = true;
        this.volume = 0f;
        this.mass = 0f;
    }

    /**
     * Creates an entity based off of 5 parameters.
     *
     * @param x
     * @param y
     * @param z
     * @param f Force vector
     * @param mass
     * @param volume
     */
    public Entity(float x, float y, float z, Vector3f f, float mass, float volume) {
        
        pos.x = x;
        pos.y = y;
        pos.z = z;
        this.force.x = f.x;
        this.force.y = f.y;
        this.force.z = f.z;
        this.ground = (pos.y < 60);
        this.volume = volume;
        this.mass = mass;
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
        this.force.x = v.force.x;
        this.force.y = v.force.y;
        this.force.z = v.force.z;
        this.velocity = v.velocity;
        this.ground = v.ground;
        this.volume = v.volume;
        this.mass = v.mass;
    }
    
    /**
     * Sets the Entity's XYZ Coordinates to something
     * 
     * @param x X to be set
     * @param y Y to be set
     * @param z Z to be set
     */
    
    public void setXYZ(float x, float y, float z)
    {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
    }
    
    /**
     * Sets an amount of force to be pushed onto entity
     * 
     * @param f Force vector
     */
    
    public void setForce(Vector3f f)
    {
        this.force.x = f.x;
        this.force.y = f.y;
        this.force.z = f.z;
    }
    
    
}
