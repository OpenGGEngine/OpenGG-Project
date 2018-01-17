/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ResourceManager {
    private static ResourceRequest current = null;
    private static List<ResourceRequest> requests = Collections.synchronizedList(new LinkedList<>());
    
    public static void initialize(){
        
    }
    
    public static void prefetch(ResourceRequest request){
        if(!isRequested(request.location))
            requests.add(request);
    }
    
    public static boolean isRequested(String pos){
        for(ResourceRequest request : requests){
            if(request.location.equalsIgnoreCase(pos)) return true;
        }
        return false;
    }
    
    public static boolean isProcessing(){
        return current != null;
    }
    
    public static ResourceRequest getCurrent(){
        return current;
    }
}
