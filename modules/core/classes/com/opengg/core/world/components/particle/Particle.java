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
    private Vector3f velocity;
    private final Vector3f gravity;
    private float lifelength = 0;
    private final float lifespan;
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
        return lifespan > 0 && lifelength > lifespan;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
