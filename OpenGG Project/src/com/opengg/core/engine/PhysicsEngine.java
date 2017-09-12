/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import com.opengg.core.physics.PhysicsEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class PhysicsEngine {
    static List<PhysicsEntity> entities = new ArrayList<>(); 
    
    public static void addEntity(PhysicsEntity entity){
        entities.add(entity);
    }
    
    public static void removeEntity(PhysicsEntity entity){
        entities.remove(entity);
    }
    
    public static void updatePhysics(float delta){
        for(PhysicsEntity entity : entities){
            entity.update(delta);
        }
    }
}
