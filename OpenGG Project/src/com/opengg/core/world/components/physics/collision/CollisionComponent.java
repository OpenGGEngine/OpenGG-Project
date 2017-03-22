/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics.collision;

import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.physics.PhysicsComponent;
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
public class CollisionComponent extends Trigger{
    BoundingBox main;
    List<Collider> boxes = new ArrayList<>();
    boolean lastcollided = false;

    public CollisionComponent(BoundingBox main, Collection<Collider> all) {
        this.main = main;
        boxes.addAll(all);
        setColliderParent();
    }
    
    public CollisionComponent(BoundingBox main, Collider... all) {
        this.main = main;
        boxes.addAll(Arrays.asList(all));
        setColliderParent();
    }
    
    private void setColliderParent(){
        for(Collider c : boxes){
            c.setParent(this);
        }
    }
    
    public void addBoundingBox(Collider bb) {
        boxes.add(bb);
    }

    public List<CollisionData> testForCollision(CollisionComponent other) {
        List<CollisionData> dataList = new ArrayList<>();
        if (!main.isColliding(other.main))
            return dataList;
        
        boolean collided = false;
        for (Collider x: this.boxes) {
            for(Collider y: other.boxes) {
                CollisionData data = x.isColliding(y);
                if ((data) != null){
                    collided = true;
                    dataList.add(data);
                }
            }
        }

        
        
        return dataList;
    }

    public PhysicsComponent getPhysicsComponent(){
        if(parent instanceof PhysicsComponent){
            return (PhysicsComponent)parent;
        }
        return null;
    }
    
    @Override
    public void update(float delta) {
        Vector3f fpos = getPosition();
        main.recenter(fpos);
    }
    
    public void collisionTrigger(List<CollisionData> data){
        if(lastcollided == false){
            TriggerInfo ti = new TriggerInfo();
            ti.info = "collision";
            ti.type = SINGLE;
            ti.source = this;
            ti.data = data;
            trigger(ti);
            lastcollided = true;
        }
    }
}
