/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics;

import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.CollisionManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class PhysicsSystem {
    PhysicsConstants constants = new PhysicsConstants();
    List<ColliderGroup> colliders = new ArrayList<>();
    List<PhysicsEntity> entities = new ArrayList<>(); 
    
    public List<PhysicsEntity> getEntities(){
        return entities;
    }
    
    public List<ColliderGroup> getColliders(){
        return colliders;
    }
    
    public void addCollider(ColliderGroup collider){
        colliders.add(collider);
    }
    
    public void removeCollider(ColliderGroup collider){
        colliders.remove(collider);
    }
    
    public void addEntity(PhysicsEntity entity){
        entities.add(entity);
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
        CollisionManager.processCollisionResponse();
    }
}
