/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities.resources;

import com.opengg.core.Matrix3f;
import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector3f;
import com.opengg.core.world.World;
import java.io.Serializable;

/**
 *
 * @author ethachu19
 */
public class PhysicsState implements Serializable {

    public EntitySupportEnums.UpdateForce updateForce;

    public Vector3f pos = new Vector3f();
    public Quaternion4f rot = new Quaternion4f();

    public Vector3f momentum = new Vector3f();
    public Vector3f angularMomentum = new Vector3f();

    public Vector3f velocity = new Vector3f();
    public Vector3f angularVelocity = new Vector3f();
    public Matrix3f inertiaTensor = new Matrix3f();
    public Quaternion4f spin = new Quaternion4f();

    public Vector3f airResistance = new Vector3f();
    public World currentWorld = null;
    public final float mass;
    public float height = 5f, width = 5f, length = 5f;

    public PhysicsState(World currentWorld, float mass) {
        switchWorld(currentWorld);
        this.mass = mass;
    }

    public PhysicsState(PhysicsState past) {
        pos = new Vector3f(past.pos);
        rot = new Quaternion4f(past.rot);
        mass = past.mass;
        height = past.height;
        width = past.width;
        length = past.length;
        currentWorld = past.currentWorld;
        airResistance = new Vector3f(past.airResistance);
        spin = new Quaternion4f(past.spin);
        angularVelocity = new Vector3f(past.angularVelocity);
        velocity = new Vector3f(past.velocity);
        angularMomentum = new Vector3f(past.angularMomentum);
        momentum = new Vector3f(momentum);
    }

    public final void recalculate() {
        velocity = momentum.divide(mass);
        angularVelocity = angularMomentum.divide(inertiaTensor);
        rot.normalize();
        spin = new Quaternion4f(0, angularVelocity.x, angularVelocity.y, angularVelocity.z).multiply(rot).multiply(0.5f);
    }

    public final void switchWorld(World next) {
        currentWorld = next;
    }

    public static void integrate(PhysicsState state, float t, float dt) {
        PhysicsDerivative a = PhysicsDerivative.evaluate(state, t);
        PhysicsDerivative b = PhysicsDerivative.evaluate(state, t, dt * 0.5f, a);
        PhysicsDerivative c = PhysicsDerivative.evaluate(state, t, dt * 0.5f, b);
        PhysicsDerivative d = PhysicsDerivative.evaluate(state, t, dt, c);

        state.pos.addEquals((a.velocity.add(2f).add(b.velocity.add(c.velocity)).add(d.velocity)).multiply(dt / 6));
        state.momentum.addEquals(a.force.add((b.force.add(c.force)).multiply(2f).add(d.force)).multiply(dt / 6f));
        state.rot.addEquals(a.spin.add(b.spin.add(c.spin)).multiply(2f).add(d.spin).multiply(dt / 6f));
        state.angularMomentum.addEquals(a.torque.add((b.torque.add(c.torque)).multiply(2f).add(d.torque).multiply(dt / 6f)));

        state.recalculate();
    }

    private static PhysicsState interpolate(final PhysicsState a, final PhysicsState b, float alpha) {
        PhysicsState state = new PhysicsState(b);
        state.pos = a.pos.multiply(1 - alpha).add(b.pos.multiply(alpha));
        state.momentum = a.momentum.multiply(1 - alpha).add(b.momentum.multiply(alpha));
        state.rot = Quaternion4f.slerp(a.rot, b.rot, alpha);
        state.angularMomentum = a.angularMomentum.multiply(1 - alpha).add(b.angularMomentum.multiply(alpha));
        state.recalculate();
        return state;
    }

    public static void forces(final PhysicsState state, float t, Vector3f force, Vector3f torque) {
//        if (state.updateForce == EntitySupportEnums.UpdateForce.Unrealistic) {
//            print ("FORCE SLEEEPENS");
//            return;
//        }
//        force.closertoZero(state.airResistance.multiply(0.5f)).add(state.currentWorld.wind).subtract(state.currentWorld.gravityVector);
//        torque.closertoZero(state.airResistance.multiply(0.2f));
//        torque.subtract(state.angularVelocity.multiply(0.2f));
        force.add((float) Math.sin(t));
        torque.add((float) Math.sin(t));
        torque.subtract(state.angularVelocity.multiply(0.2f));
    }

    public final boolean stop(int index) {
        switch (index) {
            case 0:
                momentum.x = 0;
                velocity.x = 0;
                break;
            case 1:
                momentum.y = 0;
                velocity.y = 0;
                break;
            case 2:
                momentum.z = 0;
                velocity.z = 0;
                break;
            default:
                momentum.zero();
                velocity.zero();
                break;
        }
        return true;
    }
}
