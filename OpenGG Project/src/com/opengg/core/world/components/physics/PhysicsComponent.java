/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.engine.EngineInfo;
import com.opengg.core.world.World;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.Positioned;
import com.opengg.core.world.components.Updatable;
import static com.opengg.core.world.components.physics.PhysicsConstants.BASE;

/**
 *
 * @author ethachu19
 */
public class PhysicsComponent implements Updatable, Positioned {
    
    Collider c;
    World w;
    public boolean gravEffect = true;
    public Vector3f pos = new Vector3f();
    public Quaternionf rot;
    public Vector3f force = new Vector3f();
    public Vector3f rotForce = new Vector3f();
    public Vector3f velocity = new Vector3f();
    public Vector3f angvelocity = new Vector3f();
    public Vector3f acceleration = new Vector3f();
    public Vector3f angaccel = new Vector3f();
    private Positioned parent;
    private float mass = 10f;

    @Override
    public void update(float delta) {
        delta /= 1000;
        w = EngineInfo.curworld;
        
        Vector3f last = new Vector3f(acceleration);
        pos.addEquals(velocity.multiply(delta).add(last.multiply((float) Math.pow(delta, 2) * 0.5f)));
        accel(last);
        velocity.addEquals(acceleration.multiply(delta));
        if((pos.y) < BASE){ 
            pos.y = BASE;
            velocity.y = 0;
        }
        forces(delta);
        parent.setPosition(pos);
     //   System.out.println(pos.toString());
    }
    
    public void setCollider(Collider c){
       this.c = c;
       c.setParentPhysicsComponent(this);
    }
    
    private void forces(float delta) {
        force.x += 0;
        force.y += 0;
        force.z += 0;
    }
    
    private void accel(Vector3f last){
        acceleration = force.divide(mass);
        acceleration = (last.add(acceleration)).divide(2);
        
        acceleration.y += w.gravityVector.x;
        acceleration.y += w.gravityVector.y;
        acceleration.y += w.gravityVector.z;
    }
    
    private void addGrav(Vector3f accel, World w){
        accel.y += w.gravityVector.x;
        accel.y += w.gravityVector.y;
        accel.y += w.gravityVector.z;
    }
    
    public PhysicsComponent(){}
    public PhysicsComponent(float mass){
        this.mass = mass;
    }
    
    public static PhysicsComponent interpolate(PhysicsComponent a, PhysicsComponent b, float alpha) {
        PhysicsComponent state = b;
        state.pos = a.pos.multiply(1 - alpha).add(b.pos.multiply(alpha));
        state.force = a.force.multiply(1 - alpha).add(b.force.multiply(alpha));
        state.rot = Quaternionf.slerp(a.rot, b.rot, alpha);
        state.rotForce = a.rotForce.multiply(1 - alpha).add(b.rotForce.multiply(alpha));
        return state;
    }

    @Override
    public void setParentInfo(Component parent) {
        if(parent instanceof Positioned){
            this.parent = (Positioned) parent;
            pos = this.parent.getPosition();
            return;
        }
        throw new InvalidParentException("Cannot apply physics on a positionless object!");
    }

    @Override
    public void setPosition(Vector3f pos) {}

    @Override
    public void setRotation(Vector3f rot) {}

    @Override
    public Vector3f getPosition() {
        return parent.getPosition();
    }

    @Override
    public Vector3f getRotation() {
        return parent.getRotation();
    }
}
