/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.thread;

/**
 *
 * @author Javier
 */
public class ThreadHandle {
    ThreadType tt;
    GlobalThread gt;
    Thread t;
    private ThreadHandle(int priority, ThreadType type, GameThreaded g){
        tt = type;
        gt = new GlobalThread(type,g);
        t = new Thread(gt);
        
        ThreadHandler.addHandle(this);
    }
    
    protected static ThreadHandle getHandle(int priority, ThreadType t, GameThreaded g){
        return new ThreadHandle(priority,t,g);
    }
    public void kill(){
        gt.kill();
        t = null;
    }
    public void run(){
        t.start();
    }
}
