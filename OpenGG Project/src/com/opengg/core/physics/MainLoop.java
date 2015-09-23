/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

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
        int i;
        while(true)// put in some condition? Idk
        {
            for(i = 0; iterateEntity.hasNext(); i++)
            {
                EntityList.get(i).updateXYZ();
            }
            //Calculate forces
            //Calculate direction
        }
    }
    
    public static void start()
    {
        Thread update = new Thread(new MainLoop());
        update.start();
    }
}
