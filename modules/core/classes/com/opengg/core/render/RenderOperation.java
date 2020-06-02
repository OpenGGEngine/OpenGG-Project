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
public class RenderOperation {
    private String name = "default";
    private Runnable command;
    private int priority = 5;

    private boolean enabled = true;
    private boolean reset;

    public RenderOperation(String name, Runnable e){
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void shouldResetGL(boolean reset){
        this.reset = reset;
    }
}
