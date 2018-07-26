/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

/**
 *
 * @author Javier
 */
public class RenderPath {
    String name = "default";
    Runnable command;
    int priority = 5;
    boolean enabled = true;
    boolean reset;
    public RenderPath(String name, Runnable e){
        this.name = name;
        this.command = e;
    }
    
    public void setRunnable(Runnable e){
        this.command = e;
    }
    
    public void render(){
        command.run();
    }
    
    public void setPriority(int priority){
        this.priority = priority;
    }
    
    public int getPriority(){
        return priority;
    }
    
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
    
    public boolean isEnabled(){
        return enabled;
    }
    
    public void shouldResetGL(boolean reset){
        this.reset = reset;
    }
}
