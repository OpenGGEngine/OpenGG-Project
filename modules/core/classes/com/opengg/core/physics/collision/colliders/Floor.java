package com.opengg.core.physics.collision.colliders;

import com.opengg.core.physics.collision.Collider;
import com.opengg.core.physics.collision.ContactManifold;

import java.util.Optional;

public class Floor extends Collider {
    @Override
    public Optional<ContactManifold> collide(Collider c) {
        return Optional.empty();
    }
}
