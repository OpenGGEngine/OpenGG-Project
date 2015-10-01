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
public class ThreadHandler {
    private ThreadHandler(int priority){
        
    }
    protected static ThreadHandler getHandle(int priority){
        return new ThreadHandler(priority);
    }
            
            
}
