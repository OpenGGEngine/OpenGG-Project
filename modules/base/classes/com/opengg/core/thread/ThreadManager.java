/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.thread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ThreadManager {
    private static final List<Thread> running = new ArrayList<>();
    private static final int closeDelay = 3000;
    private static Thread.UncaughtExceptionHandler handler;
    
    public static void initialize(){

    }

    public static void setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler){
        ThreadManager.handler = handler;
        for(var thread : running){
            thread.setUncaughtExceptionHandler(handler);
        }
    }

    public static Thread run(Runnable run){
        return run(run, "Default");
    }
    
    public static Thread run(Runnable run, String name){
        return run(run, name, false);
    }

    public static Thread runDaemon(Runnable run){
        return runDaemon(run, "DefaultDaemon");
    }

    public static Thread runDaemon(Runnable run, String name){
        return run(run, name, true);
    }

    public static Thread run(Runnable run, String name, boolean daemon){
        Thread thread = new Thread(run);
        thread.setDaemon(daemon);
        thread.setName(name);
        thread.start();
        running.add(thread);
        return thread;
    }
    
    public static void update(){
        List<Thread> kill = new LinkedList<>();
        for(Thread thread : running){
            if(!thread.isAlive())
                kill.add(thread);
        }
        
        for(Thread thread : kill){
            running.remove(thread);
        }
    }
    
    public static void destroy(){
        return;
        /*
        for(Thread thread : running){
            if(thread.isAlive())
                thread.interrupt();
        }
        
        try {Thread.sleep(100);} catch (InterruptedException ex) {}
        
        boolean exist = false;
        for(Thread thread : running){
            if(thread.isAlive())
                exist = true;
        }
        
        if(exist)
            GGConsole.warning("Detected threads surviving 100ms after interrupt, waiting " + closeDelay + " before forcefully removing");
        else
            return;
        
        try {Thread.sleep(closeDelay);} catch (InterruptedException ex) {}
        
        for(Thread thread : running){
            if(thread.isAlive())
                GGConsole.error("Forcing closure of thread " + thread.getName() + ", did not respond to interrupt after " + closeDelay + "ms");
            while(thread.isAlive())
                thread.stop();
        }*/
    }

    private ThreadManager() {
    }
}
