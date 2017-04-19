/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

/**
 *
 * @author Javier
 */
public class RenderPath {
    String name = "default";
    Executable command;
    int priority = 5;
    boolean enabled = true;
    boolean reset;
    public RenderPath(String name, Executable e){
        this.name = name;
        this.command = e;
    }
    
    public void setExecutable(Executable e){
        this.command = e;
    }
    
    public void render(){
        command.execute();
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
