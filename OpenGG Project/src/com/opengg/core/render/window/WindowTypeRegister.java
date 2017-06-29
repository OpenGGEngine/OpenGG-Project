/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.window;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.exceptions.WindowCreationException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Javier
 */
public class WindowTypeRegister {
    static Map<String, Window> windows = new HashMap<>();
    
    public static void registerWindowType(String name, Window window){
        GGConsole.log("Registered a window type named " + name);
        windows.put(name, window);
    }
    
    public static Window getRegisteredWindow(String name){
        Window window = windows.get(name);
        if(window == null){
            throw new WindowCreationException("Could not find a window registered with the name " + name);
        }
        return window;
    }
}
