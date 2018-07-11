/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 *
 * @author Javier
 */
public class ResourceFuture {
    private Object monitor = new Object();
    ResourceRequest request;
    private boolean processing;
    private boolean done;
    private Resource resource;
    private Consumer<Resource> func = v -> {};
    
    public boolean exists(){
        return done;
    }
    
    public ResourceFuture set(Resource resource){
        this.resource = resource;
        this.done = true;

        synchronized(monitor){
            monitor.notifyAll();
        }

        OpenGG.asyncExec(() -> func.accept(resource));

        return this;
    }
    
    public boolean isProcessing(){
        return processing;
    }
    
    public Resource get(){
        if(!done){
            synchronized(monitor){
                try{
                    monitor.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

        return resource;
    }

    public void whenComplete(Consumer<Resource> res){
        this.func = res;
    }
}
