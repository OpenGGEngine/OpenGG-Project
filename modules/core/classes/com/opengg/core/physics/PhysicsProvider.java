/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Matrix3f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.CollisionManager;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class PhysicsProvider{
    public RigidBody parent;

    public Vector3f centerOfMass = new Vector3f();
    public Matrix3f inertialMatrix = new Matrix3f(
            1f/3f,0,0,
            0,1f/3f,0,
            0,0,1f/3f);
    public Vector3f lowestContact = new Vector3f(0,-1,0);
    
    public boolean applyGravity = true;
    public boolean grounded = false;
    public boolean touched = false;
    public boolean applyRotationChangeOnCollision = true;

    private Vector3f accumulatedForces = new Vector3f();
    private Vector3f accumulatedTorques = new Vector3f();

    public Vector3f velocity = new Vector3f();
    public Vector3f angvelocity = new Vector3f();

    public float mass = 1f;
    public float density = 1f;
    
    public List<CollisionManager.ResponseSet> responses = new ArrayList<>();
    
    public PhysicsProvider(RigidBody parent){
        this.parent = parent;
    }

    public void update(float delta) {
        grounded = false;
        computeLinearMotion(delta);
        computeAngularMotion(delta);
        
        lowestContact = new Vector3f(0,-1,0);
    }
    
    private void computeLinearMotion(float delta){
        var acceleration = computeFromForces(delta);
        velocity = velocity.add(acceleration.multiply(delta));
        parent.setPosition(parent.getPosition().add(velocity.multiply(delta)));
    }
    
    private void computeAngularMotion(float delta){
        var angularAcceleration = computeFromTorques(delta);
        angvelocity = angvelocity.add(angularAcceleration.multiply(delta));
        //if(angvelocity.lengthSquared() < 0.05f) angvelocity = new Vector3f();
        parent.setRotation(parent.getRotation().multiply(Quaternionf.createXYZ(angvelocity.multiply(delta).multiply(FastMath.radiansToDegrees))).normalize());
    }
    
    private Vector3f computeFromForces(float delta) {
        var acceleration = accumulatedForces.divide(mass);
        this.accumulatedForces = new Vector3f();
        return acceleration;
    }

    private Vector3f computeFromTorques(float delta) {
        var angAccel = inertialMatrix.inverse().multiply(this.accumulatedTorques);
        this.accumulatedTorques = new Vector3f();
        return angAccel;
    }

    public void addForceForTick(Vector3f force){
        this.accumulatedForces = this.accumulatedForces.add(force);
    }

    public void addTorqueForTick(Vector3f torque){
        this.accumulatedTorques = this.accumulatedTorques.add(torque);
    }

    public void serialize(GGOutputStream out) throws IOException{
        out.write(mass);
        out.write(applyGravity);
        out.write(velocity);
        out.write(angvelocity);
    }

    public void deserialize(GGInputStream in) throws IOException{
        mass = in.readFloat();
        applyGravity = in.readBoolean();
        velocity = in.readVector3f();
        angvelocity = in.readVector3f();
    }

    public void serializeUpdate(GGOutputStream out) throws IOException{
        out.write(velocity);
        out.write(angvelocity);
    }

    public void deserializeUpdate(GGInputStream in, float delta) throws IOException{
        velocity = in.readVector3f();
        angvelocity = in.readVector3f();
        update(delta);
    }
}
