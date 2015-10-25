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
    float currentTime;
    float timeStep;
    float lastTime;
    
    public Time(){
        lastTime = currentTime;
        lastTime = System.currentTimeMillis();
    }
    public float getDeltaMs(){
        currentTime = System.currentTimeMillis();
        timeStep = currentTime - lastTime;
        lastTime = currentTime;
        return timeStep;
    }
    
    public float getDeltaSec(){
        return getDeltaMs() /1000f;
    }
}
