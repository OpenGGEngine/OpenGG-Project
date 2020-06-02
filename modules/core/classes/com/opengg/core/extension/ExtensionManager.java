/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.extension;

import com.opengg.core.console.GGConsole;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ExtensionManager {
    static List<Extension> extensions = new ArrayList<>();
    
    public static void addExtension(Extension ext){
        extensions.add(ext);
    }
    
    public static void loadStep(int requirements){
        for(var ext : extensions){
            if(!ext.initialized){
                if(ext.requirement == requirements){
                    GGConsole.log("Loading extension " + ext.extname + "...");
                    ext.loadExtension();
                    GGConsole.log("Loaded " + ext.extname + " into OpenGG successfully");
                    ext.initialized = true;
                }
            }
        }
    }
    
    public static void update(float delta){
        for(var ext : extensions){
            if(ext.initialized)
                ext.update(delta);
        }
    }
    public static void render(){
        for(var ext : extensions){
            if(ext.initialized)
                ext.render();
        }
    }

    private ExtensionManager() {
    }
    
}
