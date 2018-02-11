/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import com.opengg.core.console.GGConsole;
import com.opengg.core.thread.ThreadManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author Javier
 */
public class ResourceManager {
    public static final int THREAD_AMOUNT = 1;
    private static List<ResourceProcessorThread> threads = new ArrayList<>();
    private static List<ResourceRequest> current = Collections.synchronizedList(new LinkedList<>());
    private static Queue<ResourceRequest> requests = new PriorityBlockingQueue<>();
    
    public static void initialize(){
        for(int i = 0; i < THREAD_AMOUNT; i++){
            GGConsole.log("Initializing resource manager with " + THREAD_AMOUNT + " worker thread(s)");
            ResourceProcessorThread thread = new ResourceProcessorThread();
            ThreadManager.runRunnable(thread, "ResourceWorkerThread"+i);
            threads.add(thread);
        }
    }
    
    public static void prefetch(ResourceRequest request){
        if(!isRequested(request.location))
            requests.add(request);
    }
    
    static ResourceRequest getRequest(){
        return requests.poll();
    }
    
    public static void removePrefetch(String pos){
        ResourceRequest requestobject = null;
        for(ResourceRequest request : requests){
            if(request.location.equalsIgnoreCase(pos)) requestobject = request;
        }
        if(requestobject != null) requests.remove(requestobject);
    }
    
    public static boolean isRequested(String pos){
        for(ResourceRequest request : requests){
            if(request.location.equalsIgnoreCase(pos)) return true;
        }
        return false;
    }
    
    public static boolean isProcessing(String pos){
        for(ResourceRequest request : current){
            if(request.location.equalsIgnoreCase(pos)) return true;
        }
        return false;
    }
    
    static void addToProcessing(ResourceRequest request){
        current.add(request);
    }
    
    static void removeFromProcessing(ResourceRequest request){
        current.remove(request);
    }
    
    public static boolean isProcessing(){
        return !current.isEmpty();
    }
    
    public static List<ResourceRequest> getCurrent(){
        return current;
    }
}
