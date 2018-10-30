/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics;

import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.CollisionManager;
import com.opengg.core.physics.collision.Floor;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        floor.getColliders().add(new Floor());
        colliders.add(floor);
    }

    public List<PhysicsEntity> getEntities(){
        return entities;
    }
    
    public List<ColliderGroup> getColliders(){
        return colliders;
    }
    
    public void addCollider(ColliderGroup collider){
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
        CollisionManager.testForCollisions(this);
        CollisionManager.processCollisions();
    }

    public void serialize(GGOutputStream out) throws IOException {
        out.write(constants.BASE);
        out.write(constants.GRAVITY);
        out.write((int)entities.size());

        for(var entity : entities){
            entity.serialize(out);
        }
    }

    public void deserialize(GGInputStream in) throws IOException {
        constants.BASE = in.readFloat();
        constants.GRAVITY = in.readVector3f();

        entities.clear();
        int count = in.readInt();

        for(int i = 0; i < count; i++){

            PhysicsEntity entity = new PhysicsEntity();
            entity.deserialize(in);
            entities.add(entity);
        }

        PhysicsEntity.idcount = entities.stream()
                .mapToInt(entity -> entity.id)
                .max().orElse(0);
    }

    public PhysicsEntity getById(int id){
        for (var entity : entities) {
            if(entity.id == id) return entity;
        }
        return null;
    }
}
