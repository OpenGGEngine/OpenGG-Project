/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.render.shader.ShaderController;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Javier
 */
public class GUIController {
    static GUI current;
    static GUI defaultgui;
    static Map<String, GUI> guis = new HashMap<>();
    
    public static void initialize(){
        defaultgui = new GUI();
        addGUI(defaultgui, "default");
        useGUI("default");
    }
    
    public static void useGUI(String name){
        current = guis.get(name);
    }
    
    public static void addGUI(GUI gui, String name){
        guis.put(name, gui);
    }
        
    public static void render(){
        ShaderController.useConfiguration("gui");
        
        current.render();
    }
    
    public static GUI get(String name){
        return guis.get(name);
    }
    
    public static GUI getDefault(){
        return defaultgui;
    }
}
