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
public class ExecutableContainer {
    Executable exec;
    boolean executed = false;
    boolean sync = false;
    float timetoexec = -1;
    float elapsed = 0;
    
    public ExecutableContainer(Executable exec){
        this.exec = exec;
    }
}
