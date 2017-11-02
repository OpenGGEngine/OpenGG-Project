/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import com.opengg.core.physics.PhysicsSystem;
import com.opengg.core.physics.PhysicsEntity;

/**
 *
 * @author Javier
 */
public class PhysicsEngine {
    static PhysicsSystem instance;
    
    public static void addEntity(PhysicsEntity entity){
        instance.addEntity(entity);
    }
    
    public static void removeEntity(PhysicsEntity entity){
        instance.removeEntity(entity);
    }
    
    public static void updatePhysics(float delta){
        instance.update(delta);
    }

    public static void setInstance(PhysicsSystem physics) {
        instance = physics;
    }
    
    public static PhysicsSystem getInstance(){
        return instance;
    }
}
