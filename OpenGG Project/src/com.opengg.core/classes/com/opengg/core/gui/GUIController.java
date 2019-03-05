/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.gui;

import com.opengg.core.GGInfo;
import com.opengg.core.render.shader.ShaderController;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class GUIController {
    static Set<String> current = new HashSet<>();
    static GUI defaultgui;
    static Map<String, GUI> guis = new HashMap<>();

    static boolean enabled = true;
    
    public static void initialize(){
        defaultgui = new GUI();
        addAndUse(defaultgui, "default");
    }
    
    public static void activateGUI(String name){
        current.add(name);
        var currentGUI = guis.get(name);
        if(currentGUI.isMenu()) GGInfo.setMenu(true);
        else GGInfo.setMenu(false);
    }

    public static void deactivateGUI(String name){
        current.remove(name);
    }
    
    public static void add(GUI gui, String name){
        guis.put(name, gui);
    }

    public static void addAndUse(GUI gui, String name){
        guis.put(name, gui);
        activateGUI(name);
    }

    public static void render(){
        ShaderController.useConfiguration("gui");

        if(!enabled) return;
        current.stream().map(guis::get).forEach(GUI::render);
    }

    public static void update(float delta){
        current.stream().map(guis::get).forEach(gui -> gui.update(delta));

    }
    
    public static GUI get(String name){
        return guis.get(name);
    }
    
    public static GUI getDefault(){
        return defaultgui;
    }

    public static Set<GUI> getCurrent(){
        return current.stream().map(guis::get).collect(Collectors.toSet());
    }

    public static Set<String> getCurrentName(){
        return current;
    }

    public static void setEnabled(boolean enabled) {
        GUIController.enabled = enabled;
    }

    private GUIController() {
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
