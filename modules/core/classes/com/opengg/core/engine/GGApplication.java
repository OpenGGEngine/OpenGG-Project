/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

/**
 * An OpenGG-driven application, should be unique per application
 * @author Javier
 */
public abstract class GGApplication{
    /**
     * Name of the application, preferably human readable but not required
     */
    public String applicationName;
    
    /**
     * Application ID, used for verification for servers and world loading
     */
    public long applicationID;
    
    /**
     * Run once on application start
     */
    public abstract void setup();
    
    /**
     * Run once per render cycle, is guaranteed to have access to the render thread
     */
    public abstract void render();
    
    /**
     * Run once per update cycle, is guaranteed to have access to the update thread but may not be on the render thread
     * @param delta Time since last update cycle, in seconds
     */
    public abstract void update(float delta);
}
