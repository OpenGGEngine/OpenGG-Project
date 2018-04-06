/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

/**
 *
 * @author Javier
 */
public class ResourceFuture {
    ResourceRequest request;
    boolean processing;
    boolean done;
    Resource r;
    
    public boolean exists(){
        return done;
    }
    
    public Resource get(){
        while(!done){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) { }
        }
        return r;
    }
}
