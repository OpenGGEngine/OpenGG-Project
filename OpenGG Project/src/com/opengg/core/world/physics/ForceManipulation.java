/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.physics;

import com.opengg.core.Vector3f;
import com.opengg.core.world.entities.Entity;

/**
 *
 * @author ethachu19
 */
public class ForceManipulation {
    public Vector3f airResistance = new Vector3f();//Vector3f(1.5f,1.5f,1.5f);
    public Vector3f force = new Vector3f(0,0,0);
    private static Vector3f wind = new Vector3f(0,0,0);
    private final static float gravity = 0.5f;
    final Entity update;
    
    public ForceManipulation(Vector3f aR, Vector3f f, Entity v){
        this.airResistance.x = aR.x;
        this.airResistance.y = aR.y;
        this.airResistance.z = aR.z;
        this.force.x = f.x;
        this.force.y = f.y;
        this.force.z = f.z;
        
        update = v;
    }
    
    public ForceManipulation(Entity v){
        update = v;
    }
    
    /**
     * Calculates forces currently acting on objects
     */
    public final void calculateForces() {
        if (!update.ground) {
            if(force.x < 0)
                force.x = force.x + airResistance.x + ForceManipulation.wind.x;
            else 
                force.x = force.x - airResistance.x + ForceManipulation.wind.x;
            if(force.y < 0)
                force.y = force.y + airResistance.y - gravity + ForceManipulation.wind.y;
            else
                force.y = force.y - airResistance.y - gravity + ForceManipulation.wind.y;
            if(force.z < 0)
                force.z = force.z + airResistance.x + ForceManipulation.wind.z;
            else 
                force.z = force.z - airResistance.x + ForceManipulation.wind.z;
        } else {
            if(force.x < 0)
                force.x = force.x + airResistance.x + ForceManipulation.wind.x;
            else 
                force.x = force.x - airResistance.x + ForceManipulation.wind.x;
            if(force.z < 0)
                force.z = force.z + airResistance.x + ForceManipulation.wind.z;
            else 
                force.z = force.z - airResistance.x + ForceManipulation.wind.z;
            force.y = 0;
        }
    }
    
    /**
     * Changes Wind Force/Speed
     * 
     * @param wind Wind Vector
     */
    public static final void setWind(Vector3f wind){
        ForceManipulation.wind.x = wind.x;
        ForceManipulation.wind.y = wind.y;
        ForceManipulation.wind.z = wind.z;
    }
}
