/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import com.opengg.core.engine.OpenGG;
import static com.opengg.core.render.window.WindowOptions.GLFW;

/**
 *
 * @author Javier
 */
public class Time {
    double currentTime = 0;
    double timeStep = 0;
    double lastTime = 0;
    
    public Time(){
        lastTime = getMillis();
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
    
    double getMillis(){
        if(OpenGG.window.getType() == GLFW){
            //return ((GLFWWindow) OpenGG.window).getTime() * 1000;
        }
        return System.currentTimeMillis();
    }
}
