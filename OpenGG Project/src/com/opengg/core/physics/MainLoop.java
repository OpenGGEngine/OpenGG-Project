/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.entities.Entity;
import static com.opengg.core.entities.Entity.Collide.*;
import static com.opengg.core.entities.Entity.UpdateForce.*;
import static com.opengg.core.entities.Entity.UpdateXYZ.*;
import com.opengg.core.entities.EntityFactory;

/**
 *
 * @author ethachu19
 */
public class MainLoop extends EntityFactory implements Runnable{
    
    @Override
    public void run()
    {
        
        while(true)// put in some condition? Idk
        {
            for(Entity collide: EntityList)
            {
                collide.updateXYZ();
                if(collide.updateForce == Realistic)
                {
                    collide.calculateForces();
                }
            }
            //Calculate direction
            for(Entity collide: EntityList)
            {
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
                        collide.collisionResponse(collidee.force);
                        if(collidee.collision == Collidable)
                        {
                            collidee.collisionResponse(collide.force);
                        }
                    }
                }
            }
        }
    }
    
    public static void start()
    {
        Thread update = new Thread(new MainLoop());
        update.start();
    }
}
