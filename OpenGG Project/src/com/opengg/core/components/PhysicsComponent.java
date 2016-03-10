/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.components;

import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector3f;
import com.opengg.core.world.WorldObject;

/**
 *
 * @author ethachu19
 */
public class PhysicsComponent implements Updatable {

    public Vector3f pos;
    public Quaternion4f rot;
    public Vector3f force = new Vector3f();
    public Vector3f rotForce = new Vector3f();
    public Vector3f velocity = new Vector3f();
    public Vector3f rotVelocity = new Vector3f();
    public Vector3f acceleration = new Vector3f();
    public Vector3f rotAcceleration = new Vector3f();
    private float mass = 10f;

    @Override
    public void update(float delta) {
        Vector3f last = new Vector3f(acceleration);
        pos.addEquals(velocity.multiply(delta).add(last.multiply((float) Math.pow(delta, 2) * 0.5f)));
        acceleration = force.divide(mass);
        acceleration = (last.add(acceleration)).divide(2);
        velocity.addEquals(acceleration.multiply(delta));
        forces();
    }

    private void forces() {
        force.x += 0;
        force.y += 0;
        force.z += 0;
    }

    public PhysicsComponent(WorldObject obj) {
        this.pos = obj.pos;
        this.rot = obj.rot;
        this.mass = obj.mass;
    }

    public static PhysicsComponent interpolate(PhysicsComponent a, PhysicsComponent b, float alpha) {
        PhysicsComponent state = b;
        state.pos = a.pos.multiply(1 - alpha).add(b.pos.multiply(alpha));
        state.force = a.force.multiply(1 - alpha).add(b.force.multiply(alpha));
        state.rot = Quaternion4f.slerp(a.rot, b.rot, alpha);
        state.rotForce = a.rotForce.multiply(1 - alpha).add(b.rotForce.multiply(alpha));
        return state;
    }
}
