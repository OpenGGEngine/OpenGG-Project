/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.Vector3f;
import com.opengg.core.entities.Entity;

/**
 *
 * @author ethachu19
 */
public class ForceManipulation {
    public Vector3f airResistance = new Vector3f(1.5f,1.5f,1.5f);
    public Vector3f force = new Vector3f(0,0,0);
    public static Vector3f wind = new Vector3f(0,0,0);
    private final static float gravity = 9.8f;
    Entity update;
    
    public ForceManipulation(Vector3f aR, Vector3f f, Entity v)
    {
        this.airResistance.x = aR.x;
        this.airResistance.y = aR.y;
        this.airResistance.z = aR.z;
        this.force.x = f.x;
        this.force.y = f.y;
        this.force.z = f.z;
        
        update = v;
    }
    
    public ForceManipulation(Entity v)
    {
        update = v;
    }
    
    /**
     * Calculates forces currently acting on objects
     */
    public final void calculateForces() {
        if (!update.ground) {
            force.x = force.x - airResistance.x + ForceManipulation.wind.x;
            force.z = force.y - airResistance.z + ForceManipulation.wind.z;
            force.y = force.y - gravity + airResistance.y + ForceManipulation.wind.y;
        } else {
            force.x = force.x + airResistance.x;
            force.z = force.y + airResistance.y;
        }
    }
    
    
}
