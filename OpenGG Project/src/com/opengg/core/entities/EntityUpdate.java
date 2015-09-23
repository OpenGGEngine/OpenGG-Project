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
    public float acceleration;
    public float lastAcceleration;
    public Vector3f direction = null;
    private float timeStep;
    
    public EntityUpdate(Model m)
    {
        super(m);
        updateXYZ();
    }
    
    public void updateXYZ()
    {
        timeStep = time.getDeltaSec();
        lastAcceleration = acceleration;
        /*position += velocity * timeStep + ( 0.5 * lastAcceleration * timeStep * timeStep );
        find ratios for x to y to z movement
        */
        acceleration = force / mass;
        velocity += (lastAcceleration + acceleration ) / 2 * timeStep;
    }
}
