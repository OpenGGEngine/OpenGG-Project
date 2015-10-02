/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.thread;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class GameThreadHandle {
    public ThreadHandle r, u;
    ThreadHandle p;
    GameThreaded g;
    private GameThreadHandle(GameThreaded g){
        this.g = g;
        r = ThreadHandle.getHandle(5, ThreadType.RENDER,g);
        u = ThreadHandle.getHandle(8, ThreadType.UPDATE,g);
        p = ThreadHandle.getHandle(5, ThreadType.PHYSICS, g);
        
    }
    
    public static GameThreadHandle getHandle(GameThreaded ge){
        
        return new GameThreadHandle(ge);
    }
    public void kill(){
        r.kill();
        u.kill();
        p.kill();
        g.end();
    }
    public void run(){
        r.run();
        try {
            Thread.sleep(5);
        } catch (InterruptedException ex) {
            Logger.getLogger(GameThreadHandle.class.getName()).log(Level.SEVERE, null, ex);
        }
        u.run();
        p.kill();
    }
}
