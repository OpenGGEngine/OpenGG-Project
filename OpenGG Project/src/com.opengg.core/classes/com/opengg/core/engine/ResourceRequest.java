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
public class ResourceRequest implements Comparable{
    public static final int TEXTURE=0, MODEL=1, WORLD=2, SOUND=3;
    
    public String location;
    public int type;
    public int priority = 5;
    public boolean completed = false;
    
    public ResourceRequest(String location, int type){
        this.location = location;
        this.type = type;
    }
    
    public ResourceRequest(String location, int type, int priority){
        this.location = location;
        this.type = type;
        this.priority = priority;
    }

    @Override
    public int compareTo(Object o) {
        if(o == this) return 0;
        ResourceRequest r = (ResourceRequest) o;
        if(priority > r.priority) return 1;
        if(priority == r.priority) return 0;
        return -1;
    }
}
