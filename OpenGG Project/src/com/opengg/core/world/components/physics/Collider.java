/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import static com.opengg.core.world.components.triggers.TriggerInfo.SINGLE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author ethachu19
 */
public class Collider extends Trigger{
    PhysicsComponent pc;
    BoundingBox main;
    List<BoundingBox> boxes = new ArrayList<>();

    public Collider(BoundingBox main, Collection<BoundingBox> all) {
        this.main = main;
        boxes.addAll(all);
    }
    
    public Collider(BoundingBox main, BoundingBox... all) {
        this.main = main;
        boxes.addAll(Arrays.asList(all));
    }

    void setParentPhysicsComponent(PhysicsComponent pc){
        this.pc = pc;
    }
    
    public List<CollisionData> testForCollision(Collider other) {
        List<CollisionData> dataList = new ArrayList<>();
        if (!main.isColliding(other.main))
            return dataList;
        
        boolean trigger = false;
        for (BoundingBox x: this.boxes) {
            for(BoundingBox y: other.boxes) {
                if (x.isColliding(y)){
                    trigger = true;
                    CollisionData data = new CollisionData();
                    data.c1collider = this;
                    data.c2collider = other;
                    data.c1colliderbox = x;
                    data.c2colliderbox = y;
                    data.c1phys = pc;
                    data.c2phys = other.pc;
                    data.c1physact = pc != null;
                    data.c2physact = other.pc != null;
                    
                    dataList.add(data);
                }
            }
        }
        if (trigger) {
            TriggerInfo ti = new TriggerInfo();
            ti.info = "collision";
            ti.type = SINGLE;
            ti.source = this;
            ti.data = dataList;
            trigger(ti);
        }
        return dataList;
    }

    @Override
    public void update(float delta) {
        Vector3f fpos = getPosition();
        boxes.stream().forEach((b) -> {
            b.recenter(fpos);
        });
        main.recenter(fpos);
    }

    @Override
    public Vector3f getPosition() {
        return pc.parent.getPosition();
    }
}
