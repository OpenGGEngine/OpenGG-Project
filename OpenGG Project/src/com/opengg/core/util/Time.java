/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import com.opengg.core.engine.EngineInfo;
import static com.opengg.core.engine.EngineInfo.GLFW;
import com.opengg.core.render.window.GLFWWindow;

/**
 *
 * @author Javier
 */
public class Time {
    double currentTime = 0;
    double timeStep = 0f;
    double lastTime = 0;
    
    public Time(){
        lastTime = System.currentTimeMillis();
    }
    public float getDeltaMs(){
        currentTime = getMillis();
        timeStep = currentTime - lastTime;
        lastTime = currentTime;
        return (float) timeStep;
    }
    
    public float getDeltaSec(){
        return getDeltaMs() / 1000f;
    }
    
    public double getMillis(){
        if(EngineInfo.windowType == GLFW){
            return ((GLFWWindow) EngineInfo.window).getTime() * 1000;
        }
        return System.currentTimeMillis();
    }
}
