/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.thread;

import com.opengg.core.engine.GGConsole;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ThreadManager {
    private static List<Thread> running = new ArrayList<>();
    private static int closeDelay = 3000;
    
    public static void initialize(){
        Thread.setDefaultUncaughtExceptionHandler(new GGThreadExceptionHandler());
    }
    
    public static void runRunnable(Runnable run){
        runRunnable(run, "default");
    }
    
    public static void runRunnable(Runnable run, String name){
        Thread thread = new Thread(run);
        thread.setName(name);
        thread.start();
        running.add(thread);
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
}
