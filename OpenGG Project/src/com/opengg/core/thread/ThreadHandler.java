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
public class ThreadHandler {
    static ThreadHandle[] t = new ThreadHandle[4];
    static int counter = 0;
    private ThreadHandler(){}
    
    public static void addHandle(ThreadHandle th){
        t[counter] = th;
        counter++;
    }
            
}
