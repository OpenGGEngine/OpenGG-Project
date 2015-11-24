/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import static com.opengg.core.util.GlobalUtil.print;

/**
 *
 * @author Javier
 */
public class Time {
    long currentTime = 0;
    float timeStep = 0f;
    long lastTime = 0;
    
    public Time(){
        lastTime = System.currentTimeMillis();
    }
    public float getDeltaMs(){
        currentTime = System.currentTimeMillis();
//        print(lastTime + " " + currentTime);
        timeStep = currentTime - lastTime;
        lastTime = currentTime;
        return timeStep;
    }
    
    public float getDeltaSec(){
        return getDeltaMs() /1000f;
    }
}
