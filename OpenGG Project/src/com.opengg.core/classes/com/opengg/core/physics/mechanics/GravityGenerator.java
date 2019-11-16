package com.opengg.core.physics.mechanics;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsProvider;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;

public class GravityGenerator extends ForceGenerator {
    private Vector3f gravity = new Vector3f(0,-9.81f,0);

    @Override
    public void applyTo(PhysicsProvider entity) {
        if(entity.applyGravity){
            entity.addForceForTick(gravity.multiply(entity.mass));
        }
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        out.write(gravity);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        this.gravity = in.readVector3f();
    }
}
