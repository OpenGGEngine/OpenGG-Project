/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.thread;

import com.opengg.core.world.physics.MainLoop;

/**
 *
 * @author Javier
 */
public class GlobalThread implements Runnable{
    ThreadType t;
    GameThreaded g;
    boolean notDead = true;
    public GlobalThread(ThreadType t, GameThreaded g){
        this.t = t;
        this.g = g;
    }
    
    public GlobalThread(ThreadType t){
        this.t = t;
    }
    
    public void kill(){
        MainLoop.killProcess();
        notDead = false;
    }
    
    @Override
    public void run() {
        if(t == ThreadType.RENDER){
            g.setup();
        }
        
        
        while(notDead){
            if(t == ThreadType.RENDER){
                g.render();
                
            }
            if(t == ThreadType.UPDATE){
                g.update(1);
            }
//            if(t == ThreadType.PHYSICS){
//                MainLoop.process();
//            }
        }
    }

  
    
}
