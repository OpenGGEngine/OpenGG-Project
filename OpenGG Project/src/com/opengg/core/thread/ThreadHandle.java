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
public class ThreadHandle {
    
    private ThreadHandle(int priority, GameThreaded g){
        
    }
    
    protected static ThreadHandle getHandle(int priority, ThreadType t, GameThreaded g){
        return new ThreadHandle(priority,g);
    }
}
