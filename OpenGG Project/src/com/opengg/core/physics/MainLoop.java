/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.Vector3f;
import com.opengg.core.entities.Entity;
import static com.opengg.core.entities.Entity.Collide.*;
import static com.opengg.core.entities.Entity.UpdateForce.*;
import static com.opengg.core.entities.Entity.UpdateXYZ.*;
import com.opengg.core.entities.EntityFactory;

/**
 *
 * @author ethachu19
 */
public class MainLoop extends EntityFactory{

    public static void process()
    {
        while(true)// put in some condition? Idk
        {
            for(Entity collide: EntityList)
            {
                if(collide.updatePosition == Movable)
                {
                    collide.updateXYZ();
                }
                if(collide.updateForce == Realistic)
                {
                    collide.forceCalculator.calculateForces();
                }
            }
            for (Entity collide : EntityList) {
                if(collide.collision != Collidable)
                {
                    continue;
                }
                for(Entity collidee: EntityList)
                {
                    if(collide.equals(collidee) || collidee.collision == Uncollidable)
                    {
                        continue;
                    }
                    if(CollisionDetection.areColliding(collide, collidee) == 1)
                    {
                        if(collidee.updatePosition == Immovable)
                        {
                            collide.collisionResponse(new Vector3f(-collide.forceCalculator.force.x*2, collide.forceCalculator.force.y, -collide.forceCalculator.force.z*2));
                            continue;
                        }
                        collide.collisionResponse(collidee.forceCalculator.force);
                        if(collidee.collision == Collidable)
                        {
                            collidee.collisionResponse(collide.forceCalculator.force);
                        }
                    }
                }
            }
        }
    }
}
