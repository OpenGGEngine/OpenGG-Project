package com.opengg.core.physics.collision;

import java.util.ArrayList;
import java.util.List;

public class Floor extends Collider{
    @Override
    public List<ContactManifold> collide(Collider c) {
        return new ArrayList<>();
    }
}
