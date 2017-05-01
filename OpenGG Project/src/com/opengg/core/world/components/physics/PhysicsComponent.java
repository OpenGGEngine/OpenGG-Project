/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;
import com.opengg.core.world.collision.Collision;
import com.opengg.core.world.collision.CollisionHandler;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ComponentHolder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ethachu19
 */
public class PhysicsComponent extends ComponentHolder {
    public boolean gravEffect = true;
    public boolean grounded = false;
    public boolean touched = false;
    public boolean overrideFriction = false;
    
    CollisionComponent collider;
    
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
    
    public PhysicsComponent(){}
    
    public PhysicsComponent(float mass){
        this.mass = mass;
    }
    
    public PhysicsComponent(CollisionComponent collider){
        addCollider(collider);
    }

    @Override
    public void update(float delta) {
        pos = parent.getPosition();
        rot = parent.getRotation();
        
        float floor = getWorld().floorLev;
        
        force = finalForce();
        accel(force);
        grounded = false;
        touched = false;
        
        velocity.addThis(acceleration.multiply(delta));
        pos.addThis(velocity.multiply(delta));
        if(collider != null){
            List<Collision> collisions = CollisionHandler.testForCollisions(collider);
            for(Collision c : collisions){
                touched = true;
                if(c.collisionNormal.y > 0.5f)
                    grounded = true;
                //velocity = Vector3f.lerp(velocity, velocity.reflect(c.collisionNormal).multiply(bounciness), bounciness);
                pos.subtractThis(c.overshoot);
            }
        }      
        
        rotforce = finalRotForce();
        angaccel = rotforce.divide(mass);
        angvelocity.addThis(angaccel.multiply(delta));

        if(pos.y < floor){ 
            pos.y = floor;
            velocity.y = -velocity.y * bounciness;
            grounded = true;
            touched = true;
        }
        
        if(touched && !overrideFriction){
            velocity.multiplyThis(1-frictionCoefficient*delta);
        }
        
        parent.setPositionOffset(pos);
        parent.setRotationOffset(rot);
    }
    
    public void addCollider(CollisionComponent c){
        collider = c;
        attach(c);
    }
    
    public List<CollisionComponent> getColliders() {
        List<CollisionComponent> colliders = new ArrayList<>();
        for(Component c : children){
            if(c instanceof Component)
                colliders.add((CollisionComponent)c);
        }
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
            acceleration.x += getWorld().gravityVector.x;
            acceleration.y += getWorld().gravityVector.y;
            acceleration.z += getWorld().gravityVector.z;
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
    
    public static PhysicsComponent interpolate(PhysicsComponent a, PhysicsComponent b, float alpha) {
        PhysicsComponent state = b;
        state.pos = a.pos.multiply(1 - alpha).add(b.pos.multiply(alpha));
        state.velocity = a.velocity .multiply(1 - alpha).add(b.velocity .multiply(alpha));
        
        state.rot = Quaternionf.slerp(a.rot, b.rot, alpha);
        state.angvelocity = a.angvelocity.multiply(1 - alpha).add(b.angvelocity.multiply(alpha));
        
        state.acceleration = a.acceleration.multiply(1 - alpha).add(b.acceleration.multiply(alpha));
        state.angaccel = a.angaccel.multiply(1 - alpha).add(b.angaccel.multiply(alpha));
        return state;
    }
    
    @Override
    public void serialize(Serializer s){
        super.serialize(s);
        s.add(mass);
        s.add(density);
    }
    
    @Override
    public void deserialize(Deserializer s){
        super.deserialize(s);
        mass = s.getFloat();
        density = s.getFloat();
    }
}
