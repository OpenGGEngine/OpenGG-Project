/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.console;

import com.opengg.core.console.GGConsole;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 *
 * @author Javier
 */
public class GGThreadExceptionHandler implements UncaughtExceptionHandler{

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        GGConsole.error("Thread " + t.getName() + " with ID " +  t.getId() + " has encountered an uncaught exception");
        GGConsole.error("Caught " + e.getClass().getSimpleName() + " with message " + e.getMessage() + ": Encountered in " + e.getStackTrace()[0].getClassName());
        e.printStackTrace();
    } 
}
