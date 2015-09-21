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
    public float force;
    
    public Entity()
    {
        
    }
    
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
     * @param force
     * @param mass
     * @param volume
     */
    public Entity(float x, float y, float z, float force, float mass, float volume) {
        
        pos.x = x;
        pos.y = y;
        pos.z = z;
        this.force = force;
        this.ground = (y > 60);
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
        this.force = v.force;
        this.ground = v.ground;
        this.volume = v.volume;
        this.mass = v.mass;
    }
    
    public void setXYZ(float x, float y, float z)
    {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
    }
    
    public void setForce(float force)
    {
        this.force = force;
    }
}
