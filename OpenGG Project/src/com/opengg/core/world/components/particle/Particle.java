/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.particle;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Warren
 * 
 * This class represents a single particle
 */
public class Particle {
    private Vector3f position;

    public Vector3f getPosition() {
        return position;
    }
    private Vector3f velocity;
    private Vector3f gravity;
    private float lifelength = 0;
    private float lifespan;
    private float scale;
    
    public Particle(Vector3f position, Vector3f velocity, float lifeLength, float scale) {
        this(position, velocity, -9.81f, lifeLength, scale);
    }
    
    public Particle(Vector3f position, Vector3f velocity, float gravity, float lifeLength, float scale) {
        this(position, velocity, new Vector3f(0,gravity,0), lifeLength, scale);
    }
    
    public Particle(Vector3f position, Vector3f velocity, Vector3f gravity, float lifeLength, float scale) {
        this.position = position;
        this.velocity = velocity;
        this.gravity = gravity;
        this.lifespan = lifeLength;
        this.scale = scale;
    }
    
    public boolean update(float delta){
        velocity = velocity.add(gravity.multiply(delta));
        position = position.add(velocity.multiply(delta));

        lifelength += delta;
        return lifelength > lifespan;
    } 
}
