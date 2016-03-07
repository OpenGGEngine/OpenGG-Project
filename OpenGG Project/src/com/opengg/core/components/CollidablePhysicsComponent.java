/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.components;

import com.opengg.core.world.WorldObject;
import com.opengg.core.world.physics.BoundingBox;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author ethachu19
 */
public class CollidablePhysicsComponent extends PhysicsComponent{

    public static ArrayList<CollidablePhysicsComponent> addStack = new ArrayList<>();
    private static ArrayList<CollidablePhysicsComponent> allCollision = new ArrayList<>();

    BoundingBox main;
    ArrayList<BoundingBox> specifics = new ArrayList<>();
    WorldObject obj;

    public CollidablePhysicsComponent(WorldObject obj, BoundingBox main, Collection<BoundingBox> all) {
        super(obj);
        this.obj = obj;
        this.main = main;
        specifics.addAll(all);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        main.recenter(obj.pos);
        specifics.stream().forEach((x) -> {
            x.recenter(obj.pos);
        });
        allCollision.addAll(addStack);
    }
    
    @Override
    public boolean equals(Object x) {
        return x == this;
    }

    public boolean isColliding(CollidablePhysicsComponent test) {
        if (!main.isColliding(test.main)) {
            return false;
        }
        for (BoundingBox x: this.specifics) {
            for(BoundingBox y: test.specifics) {
                if (x.isColliding(y))
                    return true;
            }
        }
        return false;
    }
}
