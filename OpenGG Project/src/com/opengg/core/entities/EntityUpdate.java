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

/**
 *
 * @author ethachu19
 */

//need to implement air resistance
public abstract class EntityUpdate extends Entity {
    Time time = new Time();
    public Vector3f acceleration = new Vector3f();
    public Vector3f lastAcceleration = new Vector3f();
    private float timeStep;
    final float gravity = 9.8f;
    public Vector2f wind = new Vector2f();
    
    public EntityUpdate(Model m)
    {
        super(m);
        updateXYZ(); //Flushes out getDeltaSec for accurate values of timeStep
    }
    
    /**
     * Updates XYZ based on velocity and acceleration and calculates new values for all of them
     */
    
    public void updateXYZ()
    {
        timeStep = time.getDeltaSec();
        lastAcceleration = acceleration;
        pos.x += velocity.x * timeStep + ( 0.5 * lastAcceleration.x * timeStep * timeStep );
        pos.y += velocity.y * timeStep + ( 0.5 * lastAcceleration.x * timeStep * timeStep );
        pos.z += velocity.z * timeStep + ( 0.5 * lastAcceleration.x * timeStep * timeStep );
        ground = (pos.y < 60);
        acceleration.x = force.x/mass;
        acceleration.y = force.y/mass;
        acceleration.z = force.z/mass;
        velocity.x += (lastAcceleration.x + acceleration.x ) / 2 * timeStep;
        velocity.y += (lastAcceleration.y + acceleration.y ) / 2 * timeStep;
        velocity.z += (lastAcceleration.z + acceleration.z ) / 2 * timeStep;
    }
    
    /**
     * Calculates forces currently acting on objects
     */
    
    public void calculateForces()
    {
        if(!ground)
        {
            force.x = force.x + wind.x;
            force.z = force.y + wind.y;
            force.y = force.y - gravity;
        }
        else
        {
            force.x = force.x + wind.x;
            force.z = force.y + wind.y;
        }
    }
    
    /**
     * The collision response when collision with another entity is detected
     * 
     * @param force Force for how much it should react
     * @return Error
     */
    
    public boolean collisionResponse(Vector3f force)
    {
        
        
        return true;
    }
}