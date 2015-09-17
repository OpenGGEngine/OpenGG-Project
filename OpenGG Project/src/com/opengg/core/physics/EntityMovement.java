/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.entities.Entity;

/**
 *
 * @author ethachu19
 */
public class entityMovement {
    public float currentTime = System.currentTimeMillis()/1000f;
    public float lastTime = currentTime;
    public float timeStep = currentTime - lastTime;
    
    public void updateEntity(Entity x)
    {
        currentTime = System.currentTimeMillis()/1000f;
        timeStep = currentTime - lastTime;
        x.x += x.velocity * timeStep;
        x.velocity += x.acceleration * timeStep;
        
    }
}
