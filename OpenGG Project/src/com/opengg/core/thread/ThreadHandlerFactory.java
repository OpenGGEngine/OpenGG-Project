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
    
    
    public static GameThreadHandle getHandle(GameThreaded g){
        return GameThreadHandle.getHandle(g);
        
    }
    private ThreadHandlerFactory(){}
    
    
}
