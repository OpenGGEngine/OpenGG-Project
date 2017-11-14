/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.ColliderGroup;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class PhysicsEntity extends PhysicsObject{
    List<ColliderGroup> colliders = new ArrayList<>();
    Vector3f centerOfMass = new Vector3f();
    
    
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

    public float mass = 1f;
    public float density = 1f;
    public float frictionCoefficient = 0.5f;
    public float bounciness = 0.5f;
    
    public PhysicsEntity(){
        
    }
    
    public PhysicsEntity(PhysicsSystem system){
        this.system = system;
    }

    public PhysicsEntity(PhysicsSystem system, ColliderGroup collider){
        this(system);
        addCollider(collider);
    }

    public void update(float delta) {
        computeLinearMotion(delta);
        
        rotforce = finalRotForce();
        angaccel = rotforce.divide(mass);
        angvelocity.addThis(angaccel.multiply(delta));
        
        if(position.y <= getSystem().getConstants().BASE){ 
            position.y = getSystem().getConstants().BASE;
            velocity.y = -velocity.y * bounciness;
            grounded = true;
            touched = true;
        }else{
            grounded = false;
            touched = false;
        }
    }
    
    public void updateCollisionResponse(float delta){
        
    }
    
    private void computeLinearMotion(float delta){
        force = computeForces(delta);
        acceleration = getAccel(force);
        
        velocity = velocity.add(acceleration.multiply(delta));
        
        if(touched && !overrideFriction){
            velocity.multiplyThis(1-frictionCoefficient*delta);
        }
        
        position = position.add(velocity.multiply(delta));
    }
    
    private Vector3f computeForces(float delta) {
        overrideFriction = false;
        Vector3f fforce = new Vector3f();
        for(Force forcee : forces){
            if(velocity.add(fforce.multiply(mass)).multiply(delta).length() < forcee.velLimit){
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
    
    private Vector3f getAccel(Vector3f force){
        Vector3f accel = force.divide(mass);
        if (gravEffect && !grounded) {
            accel = accel.add(system.getConstants().GRAVITY);
        }
        return accel;
    }
    
        
    public void addCollider(ColliderGroup c){
        colliders.add(c);
        c.setParent(this);
    }
    
    public List<ColliderGroup> getColliders() {
        return colliders;
    }
    
    public void setSystem(PhysicsSystem system){
        if(this.system != null){
            this.system.removeEntity(this);
            for(ColliderGroup col : getColliders()){
                system.removeCollider(col);
            }
        }
        
        
        this.system = system;
        system.addEntity(this);
        for(ColliderGroup col : getColliders()){
            system.addCollider(col);
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
        
        state.rotation = Quaternionf.slerp(a.rotation, b.rotation, alpha);
        state.angvelocity = a.angvelocity.multiply(1 - alpha).add(b.angvelocity.multiply(alpha));
        
        state.acceleration = a.acceleration.multiply(1 - alpha).add(b.acceleration.multiply(alpha));
        state.angaccel = a.angaccel.multiply(1 - alpha).add(b.angaccel.multiply(alpha));
        return state;
    }
}
