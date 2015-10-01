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
        r = ThreadHandlerFactory.getHandle(5, ThreadType.RENDER);
        u = ThreadHandlerFactory.getHandle(8, ThreadType.UPDATE);
    }
    
    public static GameThreadHandle getHandle(){
        return new GameThreadHandle();
    }
}
