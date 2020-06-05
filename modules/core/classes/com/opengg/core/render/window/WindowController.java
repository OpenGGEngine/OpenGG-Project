/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.window;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Vector2i;
import com.opengg.core.vr.render.VRWindow;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class WindowController {
    static Window window;
    static Vector2i oldsize = new Vector2i(1,1);
    
    public static List<WindowResizeListener> listeners = new ArrayList<>();
    
    public static void setup(WindowOptions windowinfo){
        WindowTypeRegister.registerWindowType("GLFW", new GLFWWindow());
        WindowTypeRegister.registerWindowType("OpenVR", new VRWindow());
        
        window = WindowTypeRegister.getRegisteredWindow(windowinfo.type);
        GGConsole.log("Window registered under the name " + windowinfo.type + " requested and found, creating instance...");
        window.setup(windowinfo);
        GGInfo.setGlVersion(windowinfo.glmajor + "." + windowinfo.glminor);
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

        Vector2i newsize = new Vector2i(Math.abs(window.getWidth()), Math.abs(window.getHeight()));
        if(!newsize.equals(oldsize)){
            oldsize = newsize;
            resize(newsize);
        }
        oldsize = newsize;
    }
            
    public static void destroy(){
        if(window != null)
            window.destroy();
    }
    
    public static Vector2i getSize(){
        return oldsize;
    }
    
    public static int getWidth(){
        return oldsize.x;
    }
    
    public static int getHeight(){
        return oldsize.y;
    }
    
    public static float getRatio(){
        return oldsize.x/(float)oldsize.y;
    }

    private WindowController() {
    }
}
