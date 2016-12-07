/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities.resources;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;

/**
 *
 * @author ethachu19
 */
@Deprecated
public class PhysicsDerivative {

    Vector3f velocity = new Vector3f();
    Vector3f force = new Vector3f();
    Quaternionf spin = new Quaternionf();
    Vector3f torque = new Vector3f();

    public static PhysicsDerivative evaluate(PhysicsState state, float t) {
        PhysicsDerivative output = new PhysicsDerivative();
        output.velocity = state.velocity;
        output.spin = state.spin;
        PhysicsState.forces(state, t, output.force, output.torque);
        return output;
    }

    public static PhysicsDerivative evaluate(PhysicsState state, float t, float dt, PhysicsDerivative derivative) {
        state.pos.addEquals(derivative.velocity.multiply(dt));
        state.momentum.addEquals(derivative.force.multiply(dt));
        state.rot.addEquals(derivative.spin.multiply(dt));
        state.angularMomentum.addEquals(derivative.torque.multiply(dt));
        state.recalculate();

        PhysicsDerivative output = new PhysicsDerivative();
        output.velocity = state.velocity;
        output.spin = state.spin;
        PhysicsState.forces(state, t + dt, output.force, output.torque);
        return output;
    }
}
