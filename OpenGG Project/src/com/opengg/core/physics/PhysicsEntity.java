/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.engine.WorldEngine;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.Collision;
import com.opengg.core.physics.collision.CollisionHandler;
import com.opengg.core.world.components.physics.CollisionComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class PhysicsEntity {
    List<CollisionComponent> colliders = new ArrayList<>();
    
    public boolean gravEffect = true;
    public boolean grounded = false;
    public boolean touched = false;
    public boolean overrideFriction = false;
    
    public List<Force> forces = new LinkedList<>();
    public List<Vector3f> rotforces = new LinkedList<>();
    
    public Vector3f force = new Vector3f();
    public Vector3f rotforce = new Vector3f();
    
    public Vector3f acceleration = new Vector3f();
    public Vector3f angaccel = new Vector3f();
    
    public Vector3f velocity = new Vector3f();
    public Vector3f angvelocity = new Vector3f();
    
    public Vector3f position = new Vector3f();
    public Vector3f rotation = new Vector3f();

    public float mass = 100f;
    public float density = 1f;
    public float frictionCoefficient = 0.5f;
    public float bounciness = 0.5f;
    
    public PhysicsEntity(){}
    
    public PhysicsEntity(float mass){
        this.mass = mass;
    }
    
    public PhysicsEntity(CollisionComponent collider){
        addCollider(collider);
    }

    public void update(float delta) {
        Vector3f pos = position;
        Vector3f rot = position;
        
        float floor = WorldEngine.getCurrent().floorLev;
        
        force = finalForce();
        accel(force);
        grounded = false;
        touched = false;
        
        velocity.addThis(acceleration.multiply(delta));
        pos.addThis(velocity.multiply(delta));
        
        rotforce = finalRotForce();
        angaccel = rotforce.divide(mass);
        angvelocity.addThis(angaccel.multiply(delta));
        
        if(pos.y < floor){ 
            pos.y = floor;
            velocity.y = -velocity.y * bounciness;
            grounded = true;
            touched = true;
        }
        
        for(CollisionComponent collider : colliders){
            List<Collision> collisions = CollisionHandler.testForCollisions(collider);
            for(Collision c : collisions){
                touched = true;
                if(c.collisionNormal.y > 0.5f)
                    grounded = true;
                velocity = Vector3f.lerp(velocity, velocity.reflect(c.collisionNormal).multiply(bounciness), bounciness);
                pos.subtractThis(c.overshoot);
            }
        }      
     
        if(touched && !overrideFriction){
            velocity.multiplyThis(1-frictionCoefficient*delta);
        }
    }
    
    public void addCollider(CollisionComponent c){
        colliders.add(c);
    }
    
    public List<CollisionComponent> getColliders() {
        return colliders;
    }
    
    private Vector3f finalForce() {
        overrideFriction = false;
        Vector3f fforce = new Vector3f();
        for(Force forcee : forces){
            if(!((velocity.length() >= forcee.velLimit) || forcee.velLimit == 0 )&& forcee.force.length() != 0){
                fforce.addThis(forcee.force);
                overrideFriction = overrideFriction || forcee.frictionDisable;
            }
        }
        return fforce;
    }
    
    private Vector3f finalRotForce() {
        Vector3f fforce = new Vector3f();
        for(Vector3f forcee : rotforces){
            fforce = fforce.add(forcee);
        }
        return fforce;
    }
    
    private void accel(Vector3f force){
        acceleration = force.divide(mass);
        
        if (gravEffect && !grounded) {
            acceleration.x += WorldEngine.getCurrent().gravityVector.x;
            acceleration.y += WorldEngine.getCurrent().gravityVector.y;
            acceleration.z += WorldEngine.getCurrent().gravityVector.z;
        }
    }
    
    public void addForce(Force force){
        forces.add(force);
    }
    
    public void addAngularForce(Vector3f force){
        rotforces.add(force);
    }
    
    public void clearForces(){
        forces.clear();
        rotforces.clear();
    }
    
    public static PhysicsEntity interpolate(PhysicsEntity a, PhysicsEntity b, float alpha) {
        PhysicsEntity state = b;
        state.position = a.position.multiply(1 - alpha).add(b.position.multiply(alpha));
        state.velocity = a.velocity.multiply(1 - alpha).add(b.velocity .multiply(alpha));
        
        state.rotation = Vector3f.lerp(a.rotation, b.rotation, alpha);
        state.angvelocity = a.angvelocity.multiply(1 - alpha).add(b.angvelocity.multiply(alpha));
        
        state.acceleration = a.acceleration.multiply(1 - alpha).add(b.acceleration.multiply(alpha));
        state.angaccel = a.angaccel.multiply(1 - alpha).add(b.angaccel.multiply(alpha));
        return state;
    }
}
