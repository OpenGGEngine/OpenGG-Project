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
public class ThreadHandlerFactory {
    
    public static ThreadHandle getHandle(int priority, ThreadType t){
        return ThreadHandle.getHandle(priority,t);
        
    }
    
    public static GameThreadHandle getHandle(){
        return GameThreadHandle.getHandle();
        
    }
    private ThreadHandlerFactory(){}
    
    
}
