/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.world.components.physics.collision.CollisionComponent;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;
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
    
    public List<Vector3f> forces = new LinkedList<>();
    public List<Vector3f> rotforces = new LinkedList<>();
    
    public Vector3f force = new Vector3f();
    public Vector3f rotforce = new Vector3f();
    
    public Vector3f acceleration = new Vector3f();
    public Vector3f angaccel = new Vector3f();
    
    public Vector3f velocity = new Vector3f();
    public Vector3f angvelocity = new Vector3f();

    private float mass = 10f;
    private float density = 1f;
    
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
        delta /= 1000;
        
        float floor = getWorld().floorLev;
        
        force = finalForce();
        accel(force);
        velocity.addEquals(acceleration.multiply(delta));
        pos.addEquals(velocity.multiply(delta));
        
        rotforce = finalRotForce();
        angaccel = rotforce.divide(mass);
        angvelocity.addEquals(angaccel.multiply(delta));

        
        if(pos.y < floor){ 
            pos.y = floor;
            velocity.y = 0;
        }
        
        parent.setPositionOffset(pos);
        parent.setRotationOffset(rot);
    }
    
    public void addCollider(CollisionComponent c){
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
        Vector3f fforce = new Vector3f();
        for(Vector3f forcee : forces){
            fforce = fforce.add(forcee);
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
        
        if (gravEffect) {
            acceleration.x += getWorld().gravityVector.x;
            acceleration.y += getWorld().gravityVector.y;
            acceleration.z += getWorld().gravityVector.z;
        }
    }
    
    public void addForce(Vector3f force){
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
