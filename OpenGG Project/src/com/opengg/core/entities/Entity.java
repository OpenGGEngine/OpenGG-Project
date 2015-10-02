/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.entities;

import com.opengg.core.Model;
import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import com.opengg.core.util.Time;
import static com.opengg.core.entities.Entity.EntityType.*;
import static com.opengg.core.entities.Entity.Collide.*;
import static com.opengg.core.entities.Entity.UpdateXYZ.*;
import static com.opengg.core.entities.Entity.UpdateForce.*;

/**
 *
 * @author ethachu19
 */
public class Entity {

    public enum EntityType {
        /* Update Movement, Force Update, No Collsion Response*/

        Static,
        /* Update Movement, Force Update, Collision Detection*/ Physics,
        /* Update Movement, No force update, No Collision*/ Particle,
        /* User Defined*/ Other
    }

    public enum Collide {

        Collidable, Uncollidable, NoResponse
    }

    public enum UpdateXYZ {

        Movable, Immovable
    }

    public enum UpdateForce {

        Realistic, Unrealistic
    }
    
    /* tags */
    public UpdateForce updateForce;
    public UpdateXYZ updatePosition;
    public Collide collision;
    
    public Vector3f pos = new Vector3f();
    public float volume;
    public boolean ground;
    public float mass;
    public Vector3f force = new Vector3f();
    public Vector3f velocity = new Vector3f();
    public Vector2f direction = new Vector2f();

    private final Time time = new Time();
    public Vector3f acceleration = new Vector3f();
    public Vector3f lastAcceleration = new Vector3f();
    private float timeStep;
    final static float gravity = 9.8f;
    public static Vector2f wind = new Vector2f();

    public Entity() {

    }

    /**
     * Makes default Entity
     *
     * @param model Model to be bound to Entity
     * @param type Type of Entity
     */
    public Entity(Model model, EntityType type) {
        setXYZ(0f, 0f, 0f);
        this.ground = true;
        this.volume = 0f;
        this.mass = 0f;
        switch (type) {
            case Static:
                updatePosition = Movable;
                updateForce = Realistic;
                collision = NoResponse;
                break;

            case Physics:
                updatePosition = Movable;
                updateForce = Realistic;
                collision = Collidable;
                break;

            case Particle:
                updatePosition = Movable;
                updateForce = Unrealistic;
                collision = Uncollidable;
                break;
        }
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
     * @param type
     */
    public Entity(float x, float y, float z, Vector3f f, float mass, float volume, EntityType type) {

        setXYZ(x, y, z);
        setForce(f);
        this.ground = (pos.y < 60);
        this.volume = volume;
        this.mass = mass;
        switch (type) {
            case Static:
                updatePosition = Movable;
                updateForce = Realistic;
                collision = NoResponse;
                break;

            case Physics:
                updatePosition = Movable;
                updateForce = Realistic;
                collision = Collidable;
                break;

            case Particle:
                updatePosition = Movable;
                updateForce = Unrealistic;
                collision = Uncollidable;
                break;
        }
    }

    /**
     * Creates a new vector based off another.
     *
     * @param v Entity to be copied
     */
    public Entity(Entity v) {

        setXYZ(v.pos.x, v.pos.y, v.pos.z);
        setForce(v.force);
        setVelocity(v.velocity);
        this.ground = (pos.y < 60);
        this.volume = v.volume;
        this.mass = v.mass;

        this.collision = v.collision;
        this.updatePosition = v.updatePosition;
        this.updateForce = v.updateForce;
    }

    /**
     * Sets the entity's tags
     * 
     * @param collision The tag for collision
     * @param updateForce The tag for realistic movement
     * @param updatePosition The tag for movement
     * @throws IllegalStateException for optimization
     */
    
    public void setTags(Collide collision, UpdateForce updateForce, UpdateXYZ updatePosition) throws IllegalStateException {
        this.collision = collision;
        if(updatePosition == Immovable && updateForce == Realistic)
        {
            throw new IllegalStateException("Cannot have force without moving");
        }
        this.updateForce = updateForce;
        this.updatePosition = updatePosition;
    }
    
    /**
     * Sets the tag based on predefined entity types
     * 
     * @param type Type of entity to be set
     */
    
    public void setTags(EntityType type) {
        switch (type) {
            case Static:
                updatePosition = Movable;
                updateForce = Realistic;
                collision = NoResponse;
                break;

            case Physics:
                updatePosition = Movable;
                updateForce = Realistic;
                collision = Collidable;
                break;

            case Particle:
                updatePosition = Movable;
                updateForce = Unrealistic;
                collision = Uncollidable;
                break;
        }
    }

    /**
     * Sets the Entity's XYZ Coordinates to something
     *
     * @param x X to be set
     * @param y Y to be set
     * @param z Z to be set
     */
    public final void setXYZ(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
    }

    /**
     * Sets an amount of force to be pushed onto entity
     *
     * @param f Force vector
     */
    public final void setForce(Vector3f f) {
        this.force.x = f.x;
        this.force.y = f.y;
        this.force.z = f.z;
    }

    /**
     * Sets velocity to a vector.
     *
     * @param v Vector for velocity
     */
    public final void setVelocity(Vector3f v) {
        this.velocity.x = v.x;
        this.velocity.y = v.y;
        this.velocity.z = v.z;
    }

    /**
     * Updates XYZ based on velocity and acceleration and calculates new values
     * for all of them
     */
    public void updateXYZ() {
        timeStep = time.getDeltaSec();
        lastAcceleration = acceleration;
        pos.x += velocity.x * timeStep + (0.5 * lastAcceleration.x * timeStep * timeStep);
        pos.y += velocity.y * timeStep + (0.5 * lastAcceleration.x * timeStep * timeStep);
        pos.z += velocity.z * timeStep + (0.5 * lastAcceleration.x * timeStep * timeStep);
        ground = (pos.y < 60);
        acceleration.x = force.x / mass;
        acceleration.y = force.y / mass;
        acceleration.z = force.z / mass;
        velocity.x += (lastAcceleration.x + acceleration.x) / 2 * timeStep;
        velocity.y += (lastAcceleration.y + acceleration.y) / 2 * timeStep;
        velocity.z += (lastAcceleration.z + acceleration.z) / 2 * timeStep;
    }

    /**
     * Calculates forces currently acting on objects
     */
    public void calculateForces() {
        if (!ground) {
            force.x = force.x + Entity.wind.x;
            force.z = force.y + Entity.wind.y;
            force.y = force.y - Entity.gravity;
        } else {
            force.x = force.x + Entity.wind.x;
            force.z = force.y + Entity.wind.y;
        }
    }

    /**
     * The collision response when collision with another entity is detected
     *
     * @param force Force for how much it should react
     * @return Error
     */
    public boolean collisionResponse(Vector3f force) {

        return true;
    }

}
