/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.physics;

import com.opengg.core.Vector3f;
import static com.opengg.core.util.GlobalUtil.print;
import com.opengg.core.world.entities.Entity;
import static com.opengg.core.world.entities.Entity.*;
import com.opengg.core.world.entities.EntityFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ethachu19
 */
public class MainLoop extends EntityFactory{
    private static boolean shouldClose = false;
    
    public static void process(){
        shouldClose = false;
        while(!shouldClose){
//            print("in loop");
            if(EntityList.size() > entityCap)
                for(int i = 0; EntityList.size() > entityCap; ++i)
                    EntityFactory.destroyEntity(i);
            for(Entity collide: EntityList){
                if(collide.updatePosition == UpdateXYZ.Movable)
                    collide.updateXYZ();
                if(collide.updateForce == UpdateForce.Realistic)
                    collide.forceCalculator.calculateForces();
            }
            for (Entity collide : EntityList){
                if(collide.collision != Collide.Collidable)
                    continue;
                for(Entity collidee: EntityList){
                    if(collide.equals(collidee) || collidee.collision == Collide.Uncollidable)
                        continue;
                    if(CollisionDetection.areColliding(collide, collidee)){
                        if(collidee.updatePosition == UpdateXYZ.Immovable){
                            collide.collisionResponse(new Vector3f(-collide.forceCalculator.force.x*2, collide.forceCalculator.force.y, -collide.forceCalculator.force.z*2));
                            continue;
                        }
                        collide.collisionResponse(collidee.forceCalculator.force);
                        if(collidee.collision == Collide.Collidable)
                            collidee.collisionResponse(collide.forceCalculator.force);
                    }
                }
            }
            for (int i = 0; i < AddStack.size(); i++)
                    EntityList.add(AddStack.get(i));
            AddStack.clear();
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainLoop.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void killProcess(){
        MainLoop.shouldClose = true;
    }
}
