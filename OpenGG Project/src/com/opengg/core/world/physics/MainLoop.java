/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.physics;

import com.opengg.core.Vector3f;
import com.opengg.core.world.entities.Entity;
import static com.opengg.core.world.entities.Entity.*;

/**
 *
 * @author ethachu19
 */
public class MainLoop{

    public static void process()
    {
        while(true)// put in some condition? Idk
        {
            for(Entity collide: Entity.EntityList)
            {
                if(collide.updatePosition == UpdateXYZ.Movable)
                    collide.updateXYZ();
                if(collide.updateForce == UpdateForce.Realistic)
                    collide.forceCalculator.calculateForces();
            }
            for (Entity collide : Entity.EntityList) {
                if(collide.collision != Collide.Collidable)
                    continue;
                for(Entity collidee: Entity.EntityList)
                {
                    if(collide.equals(collidee) || collidee.collision == Collide.Uncollidable)
                        continue;
                    if(CollisionDetection.areColliding(collide, collidee))
                    {
                        if(collidee.updatePosition == UpdateXYZ.Immovable)
                        {
                            collide.collisionResponse(new Vector3f(-collide.forceCalculator.force.x*2, collide.forceCalculator.force.y, -collide.forceCalculator.force.z*2));
                            continue;
                        }
                        collide.collisionResponse(collidee.forceCalculator.force);
                        if(collidee.collision == Collide.Collidable)
                            collidee.collisionResponse(collide.forceCalculator.force);
                    }
                }
            }
        }
    }
}
