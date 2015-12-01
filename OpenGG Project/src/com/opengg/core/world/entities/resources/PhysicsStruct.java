/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities.resources;

import com.opengg.core.Vector3f;
import com.opengg.core.world.World;

/**
 *
 * @author ethachu19
 */
public class PhysicsStruct {
    
    public static Vector3f wind = new Vector3f();
    
    public Vector3f force = new Vector3f();
    public Vector3f airResistance = new Vector3f();
    public Vector3f velocity = new Vector3f();
    public Vector3f acceleration = new Vector3f();
    private World current = null;
    private final Vector3f gravityVector = new Vector3f();
    public final float mass;
    
    public PhysicsStruct(World current, float mass){
        switchWorld(current);
        this.mass = mass;
    }

    public final void update(float timeStep) {
        Vector3f lastAcceleration = new Vector3f(acceleration);
        acceleration = force.divide(mass);
        velocity = velocity.add(lastAcceleration.add(acceleration).divide(2f).multiply(timeStep));
        force = force.closertoZero(airResistance).add(wind).subtract(gravityVector);
    }
    
    public final void switchWorld(World next){
        current = next;
        gravityVector.y = current.gravity;
    }
    
    public final boolean stop(int index){
        switch (index){
            case 0:
                force.x = 0;
                velocity.x = 0;
                acceleration.x = 0;
                break;
            case 1:
                force.y = 0;
                velocity.y = 0;
                acceleration.y = 0;
                break;
            case 2:
                force.z = 0;
                velocity.z = 0;
                acceleration.z = 0;
                break;
            default:
                force.zero();
                velocity.zero();
                acceleration.zero();
                break;
        }
        return true;
    }
    
    public final void changeWind(Vector3f w){
        wind = new Vector3f(w);
    }
}
