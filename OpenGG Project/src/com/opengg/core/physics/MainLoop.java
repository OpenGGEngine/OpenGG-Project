/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import com.opengg.core.entities.EntityFactory;
import com.opengg.core.entities.PhysicsEntity;
import java.util.Iterator;

/**
 *
 * @author ethachu19
 */
public class MainLoop extends EntityFactory implements Runnable{
    
    CollisionDetection collision;

    public MainLoop() {
        this.collision = new CollisionDetection();
    }
    @Override
    public void run()
    {
        Iterator iterateEntity = EntityList.iterator();
        Iterator iteratePhysics = PhysicsList.iterator();
        int i;
        int x;
        while(true)// put in some condition? Idk
        {
            for(i = 0; iterateEntity.hasNext(); i++)
            {
                EntityList.get(i).updateXYZ();
                EntityList.get(i).calculateForces();
                iterateEntity.next();
            }
            //Calculate direction
            for(i = 0; iteratePhysics.hasNext(); i++)
            {
                for(x = 0; iterateEntity.hasNext(); x++)
                {
                    if(PhysicsList.get(i).equals(EntityList.get(x)))
                    {
                        continue;
                    }
                    if(collision.areColliding(i, x) == 1)
                    {
                        PhysicsList.get(i).collisionResponse(EntityList.get(x).velocity);
                        if(EntityList.get(x) instanceof PhysicsEntity)
                        {
                            EntityList.get(x).collisionResponse(PhysicsList.get(i).velocity);
                        }
                    }
                    iterateEntity.next();
                }
                iteratePhysics.next();
            }
        }
    }
    
    public static void start()
    {
        Thread update = new Thread(new MainLoop());
        update.start();
    }
}
