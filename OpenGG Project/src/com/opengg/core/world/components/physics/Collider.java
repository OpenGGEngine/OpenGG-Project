/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import static com.opengg.core.world.components.triggers.TriggerInfo.SINGLE;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author ethachu19
 */
public class Collider extends Trigger{

    PhysicsComponent pc;
    BoundingBox main;
    ArrayList<BoundingBox> boxes = new ArrayList<>();

    public Collider(BoundingBox main, Collection<BoundingBox> all) {
        this.main = main;
        boxes.addAll(all);
    }

    void setParentPhysicsComponent(PhysicsComponent pc){
        this.pc = pc;
    }
    
    public CollisionData testForCollision(Collider other) {
        if (!main.isColliding(other.main))
            return null;
 
        for (BoundingBox x: this.boxes) {
            for(BoundingBox y: other.boxes) {
                if (x.isColliding(y)){
                    
                    CollisionData data = new CollisionData();
                    data.c1collider = this;
                    data.c2collider = other;
                    data.c1colliderbox = x;
                    data.c2colliderbox = y;
                    data.c1phys = pc;
                    data.c2phys = other.pc;
                    data.c1physact = pc != null;
                    data.c2physact = other.pc != null;
                    
                    TriggerInfo ti = new TriggerInfo();
                    ti.info = "collision";
                    ti.type = SINGLE;
                    ti.source = this;
                    ti.data = data;
                    trigger(ti);
                    return data;
                }
            }
        }return null;
    }

    @Override
    public void setParentInfo(Component parent) {}

    @Override
    public void update(float delta) {}

}
