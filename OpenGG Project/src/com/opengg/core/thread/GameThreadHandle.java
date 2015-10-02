/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.thread;

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
    }
    public void run(){
        r.run();
        u.run();
        p.kill();
    }
}
