package com.opengg.core.physics.mechanics;

import com.opengg.core.physics.PhysicsProvider;
import com.opengg.core.physics.PhysicsObject;

public abstract class ForceGenerator extends PhysicsObject {
    public abstract void applyTo(PhysicsProvider entity);
}
