package com.opengg.core.physics.mechanics;

import com.opengg.core.physics.PhysicsEntity;
import com.opengg.core.physics.PhysicsObject;

public abstract class Field extends PhysicsObject {
    public abstract void applyTo(PhysicsEntity entity);
}
