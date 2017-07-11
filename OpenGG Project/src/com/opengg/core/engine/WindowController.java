/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import com.opengg.core.math.Vector2i;
import com.opengg.core.render.window.GLFWWindow;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.render.window.WindowResizeListener;
import com.opengg.core.render.window.WindowTypeRegister;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class WindowController {
    static Window window;
    static Vector2i oldsize;
    
    public static List<WindowResizeListener> listeners = new ArrayList<>();
    
    public static void setup(WindowInfo windowinfo){
        WindowTypeRegister.registerWindowType("GLFW", new GLFWWindow());
        
        Window twin = WindowTypeRegister.getRegisteredWindow(windowinfo.type);
        GGConsole.log("Window registered under the name " + windowinfo.type + " requested and found, creating instance...");
        twin.setup(windowinfo);
        window = twin;
        oldsize = new Vector2i(windowinfo.width, windowinfo.height);
    }
    
    public static void addResizeListener(WindowResizeListener listener){
        listeners.add(listener);
    }
    
    private static void resize(Vector2i size){
        for(WindowResizeListener listener : listeners){
            listener.onResize(size);
        }
    }
    
    public static Window getWindow(){
        return window;
    }
    
    public static void update(){
        Vector2i nsize = new Vector2i(window.getWidth(), window.getHeight());
        if(!nsize.equals(oldsize)){
            resize(nsize);
        }
        oldsize = nsize;
    }
            
    public static void destroy(){
        window.destroy();
    }
}
