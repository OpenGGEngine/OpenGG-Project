/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

import static com.opengg.core.entities.Entity.EntityType.*;
import com.opengg.core.entities.EntityFactory;
import java.util.Iterator;

/**
 *
 * @author ethachu19
 */
public class MainLoop extends EntityFactory implements Runnable{
    
    @Override
    public void run()
    {
        Iterator iterateEntity = EntityList.iterator();
        Iterator iteratePhysics = EntityList.iterator();
        int i, x;
        
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
                if(EntityList.get(i).type == Static || EntityList.get(i).type == Particle)
                {
                    iterateEntity.next();
                    continue;
                }
                for(x = 0; iterateEntity.hasNext(); x++)
                {
                    if(EntityList.get(i).equals(EntityList.get(x)))
                    {
                        iteratePhysics.next();
                        continue;
                    }
                    if(CollisionDetection.areColliding(i, x) == 1)
                    {
                        EntityList.get(i).collisionResponse(EntityList.get(x).force);
                        if(EntityList.get(x).type == Physics)
                        {
                            EntityList.get(x).collisionResponse(EntityList.get(i).force);
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
