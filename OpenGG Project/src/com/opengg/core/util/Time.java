/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

/**
 *
 * @author Javier
 */
public class Time {
    static float currentTime;
    static float timeStep;
    static float lastTime;
    
    static{
        lastTime = 1;
    }
    private Time(){
        
    }
    public static float getDelta(){
        currentTime = System.currentTimeMillis();
        timeStep = currentTime - lastTime;
        lastTime = currentTime;
        return timeStep;
    }
}
