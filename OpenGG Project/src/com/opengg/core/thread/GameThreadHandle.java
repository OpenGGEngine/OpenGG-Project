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
    ThreadHandle r, u;
    private GameThreadHandle(){
        r = ThreadHandle.getHandle(5, ThreadType.RENDER);
        u = ThreadHandle.getHandle(8, ThreadType.UPDATE);
    }
    
    public static GameThreadHandle getHandle(GameThreaded g){
        return new GameThreadHandle();
    }
}
