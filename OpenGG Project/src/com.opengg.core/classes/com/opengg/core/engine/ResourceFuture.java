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
    private boolean processing;
    private boolean done;
    private Resource r;
    
    public boolean exists(){
        return done;
    }
    
    ResourceFuture set(Resource r){
        this.r = r;
        this.done = true;
        return this;
    }
    
    public boolean isProcessing(){
        return processing;
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
