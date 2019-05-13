/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics;

import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.CollisionManager;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Javier
 */
public class PhysicsSystem {
    private final ColliderGroup floor = new ColliderGroup();

    private final PhysicsConstants constants = new PhysicsConstants();
    private List<ColliderGroup> colliders = new ArrayList<>();
    private List<PhysicsEntity> entities = new ArrayList<>();

    public PhysicsSystem(){
        floor.setForceTest(true);
        floor.getColliders().add(new ConvexHull(List.of(
                new Vector3f(-10000,constants.BASE-20,-10000),
                new Vector3f(-10000,constants.BASE-20,10000),
                new Vector3f(-10000,constants.BASE,-10000),
                new Vector3f(-10000,constants.BASE,10000),
                new Vector3f(10000,constants.BASE-20,-10000),
                new Vector3f(10000,constants.BASE-20,10000),
                new Vector3f(10000,constants.BASE,-10000),
                new Vector3f(10000,constants.BASE,10000)
        )));
        //floor.setPosition(new Vector3f(0, constants.BASE ,0));
        colliders.add(floor);
    }

    public List<PhysicsEntity> getEntities(){
        return entities;
    }
    
    public List<ColliderGroup> getColliders(){
        return colliders;
    }
    
    public void addCollider(ColliderGroup collider){
        if(colliders.contains(collider)) return;
        colliders.add(collider);
        collider.system = this;
        collider.onSystemChange();
    }
    
    public void removeCollider(ColliderGroup collider){
        colliders.remove(collider);
    }
    
    public void addEntity(PhysicsEntity entity){
        entities.add(entity);
        entity.system = this;
        entity.onSystemChange();
    }
    
    public void removeEntity(PhysicsEntity entity){
        entities.remove(entity);
    }

    public PhysicsConstants getConstants(){
        return constants;
    }
    
    public void update(float delta) {
        CollisionManager.clearCollisions();
        for(PhysicsEntity entity : entities){
            entity.update(delta);
        }
        CollisionManager.runCollisionStep(this);
    }

    public void serialize(GGOutputStream out) throws IOException {
        out.write(constants.BASE);
        out.write(constants.GRAVITY);

        out.write(entities.size());
        for(var entity : entities){
            entity.serialize(out);
        }

        out.write(colliders.size());
        for(var collider : colliders){
            collider.serialize(out);
        }
    }

    public void deserialize(GGInputStream in) throws IOException {
        constants.BASE = in.readFloat();
        constants.BASE = 1f;
        constants.GRAVITY = in.readVector3f();

        entities.clear();
        int count = in.readInt();

        for(int i = 0; i < count; i++){
            PhysicsEntity entity = new PhysicsEntity();
            entity.deserialize(in);
            addEntity(entity);
        }

        count = in.readInt();

        for(int i = 0; i < count; i++){
            ColliderGroup collider = new ColliderGroup();
            collider.deserialize(in);
            addCollider(collider);
        }

        PhysicsEntity.idcount = Stream.concat(entities.stream(), colliders.stream())
                .mapToInt(PhysicsObject::getId)
                .max().orElse(0);
    }

    public PhysicsEntity getEntityById(int id){
        for (var entity : entities) {
            if(entity.id == id) return entity;
        }
        return null;
    }

    public ColliderGroup getColliderById(int id){
        for (var collider : colliders) {
            if(collider.id == id) return collider;
        }
        return null;
    }
}
