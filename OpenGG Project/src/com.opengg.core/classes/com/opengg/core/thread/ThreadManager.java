/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.thread;

import com.opengg.core.console.GGConsole;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ThreadManager {
    private static final List<Thread> running = new ArrayList<>();
    private static int closeDelay = 3000;
    
    public static void initialize(){
        Thread.setDefaultUncaughtExceptionHandler(new GGThreadExceptionHandler());
    }
    
    public static Thread runRunnable(Runnable run){
        return runRunnable(run, "default");
    }
    
    public static Thread runRunnable(Runnable run, String name){
        Thread thread = new Thread(run);
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
        }
    }

    private ThreadManager() {
    }
}
