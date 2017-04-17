/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

/**
 * Used for asynchronous execution of any tasks that require access to the main thread
 * @author Javier
 */
public interface Executable {
    /**
     * Method called during execution, override in lambda expression or directly
     * To use, call {@code OpenGG.addExecutable(() -> {
     *          //Async code here
     *      });}
     */
    public void execute();
}
