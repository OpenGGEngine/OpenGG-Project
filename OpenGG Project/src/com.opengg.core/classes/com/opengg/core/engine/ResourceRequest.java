/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Javier
 */
public class ResourceRequest implements Comparable{

    public String location;
    public Type type;
    public int priority = 5;
    public CompletableFuture<Resource> future;
    
    public ResourceRequest(String location, Type type){
        this.location = location;
        this.type = type;
    }
    
    public ResourceRequest(String location, Type type, int priority){
        this.location = location;
        this.type = type;
        this.priority = priority;
    }

    @Override
    public int compareTo(Object o) {
        if(o == this) return 0;
        ResourceRequest r = (ResourceRequest) o;
        return Integer.compare(priority, r.priority);
    }

    public enum Type{
        TEXTURE, MODEL, WORLD, SOUND
    }
}
