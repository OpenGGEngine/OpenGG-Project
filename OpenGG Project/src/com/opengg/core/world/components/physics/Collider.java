/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.math.Vector3f;
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
    
    public void addBoundingBox(BoundingBox bb) {
        boxes.add(bb);
    }
    
    public String toString() {
        String ret = "[";
        for (BoundingBox box: boxes) {
            ret += box.toString();
            ret += " , ";
        }
        ret += "]";
        return ret;
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
                    
                    if(parent instanceof PhysicsComponent){
                        data.c1phys = (PhysicsComponent)parent;
                        data.c1physact = true;
                    }
                    if(other.parent instanceof PhysicsComponent){
                        data.c2phys = (PhysicsComponent)other.parent;
                        data.c2physact = true;
                    }
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
        for(BoundingBox b : boxes){
            b.recenter(fpos);
        }
        main.recenter(fpos);
    }
}
