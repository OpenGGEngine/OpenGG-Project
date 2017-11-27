/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.extension;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.math.Tuple;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ExtensionManager {
    static List<Tuple<Extension, Boolean>> extensions = new ArrayList<>();
    
    public static void addExtension(Extension ext){
        extensions.add(new Tuple(ext,false));
    }
    
    public static void loadStep(int requirements){
        for(Tuple<Extension, Boolean> pair : extensions){
            if(pair.y == false){
                if(pair.x.requirement == requirements){
                    GGConsole.log("Loading extension " + pair.x.extname + "...");
                    pair.x.loadExtension();
                    GGConsole.log("Loaded " + pair.x.extname + " into OpenGG successfully");
                    pair.y = true;
                }
            }
        }
    }
    
    public static void update(float delta){
        for(Tuple<Extension, Boolean> pair : extensions){
            if(pair.y == true)
                pair.x.update(delta);
        }
    }
    public static void render(){
        for(Tuple<Extension, Boolean> pair : extensions){
            if(pair.y == true)
                pair.x.render();
        }
    }
    
}
