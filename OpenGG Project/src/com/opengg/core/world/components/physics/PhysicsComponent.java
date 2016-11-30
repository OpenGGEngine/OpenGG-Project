/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector3f;
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
    
    public boolean gravEffect = true;
    public Vector3f pos = new Vector3f();
    public Quaternion4f rot;
    public Vector3f force = new Vector3f();
    public Vector3f rotForce = new Vector3f();
    public Vector3f velocity = new Vector3f();
    public Vector3f rotVelocity = new Vector3f();
    public Vector3f acceleration = new Vector3f();
    public Vector3f rotAcceleration = new Vector3f();
    private Positioned parent;
    private float mass = 10f;

    @Override
    public void update(float delta) {
        World w = EngineInfo.curworld;
        Vector3f last = new Vector3f(acceleration);
        pos.addEquals(velocity.multiply(delta).add(last.multiply((float) Math.pow(delta, 2) * 0.5f)));
        acceleration = force.divide(mass);
        acceleration = (last.add(acceleration)).divide(2);
        
        addGrav(acceleration,w);
        
        velocity.addEquals(acceleration.multiply(delta));

        if((pos.y) < BASE){ 
            pos.y = BASE;
            velocity.y = 0;
        }
        forces(delta);
        parent.setPosition(pos);
    }

    private void forces(float delta) {
        force.x += 0;
        force.y += 0;
        force.z += 0;
    }
    
    private void addGrav(Vector3f accel, World w){
        accel.y += w.gravityVector.x;
        accel.y += w.gravityVector.y;
        accel.y += w.gravityVector.z;
    }
    
    public PhysicsComponent(){
        
    }
    
    public static PhysicsComponent interpolate(PhysicsComponent a, PhysicsComponent b, float alpha) {
        PhysicsComponent state = b;
        state.pos = a.pos.multiply(1 - alpha).add(b.pos.multiply(alpha));
        state.force = a.force.multiply(1 - alpha).add(b.force.multiply(alpha));
        state.rot = Quaternion4f.slerp(a.rot, b.rot, alpha);
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
        throw new InvalidParentException("Cannot set an object with no position as having physics!");
    }

    @Override
    public void setPosition(Vector3f pos) {
        
    }

    @Override
    public void setRotation(Vector3f rot) {
        
    }

    @Override
    public Vector3f getPosition() {
        return pos;
    }

    @Override
    public Vector3f getRotation() {
        return new Vector3f();
    }
}
