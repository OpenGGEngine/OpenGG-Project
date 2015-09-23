/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.entities;

import com.opengg.core.Model;
import com.opengg.core.Vector3f;
import com.opengg.core.util.Time;

/**
 *
 * @author ethachu19
 */
public abstract class EntityUpdate extends Entity {
    Time time = new Time();
    public Vector3f acceleration = new Vector3f();
    public Vector3f lastAcceleration = new Vector3f();
    private float timeStep;
    
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
    
    public void calculateForces()
    {
        
    }
}
