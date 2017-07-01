/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Warren
 */
public class TextureManager {
    private static Map<String, TextureData> texturelist = new HashMap<>();
    private static TextureData defaultdata;
    
    public static void initialize(){
        try {
            File deft = new File(Resource.getTexturePath("default.png"));
            if(deft.exists()){
                String defPath = deft.getCanonicalPath();
                defaultdata = TextureLoader.loadTexture(defPath);
            }else{
                GGConsole.error("Failed to load the default texture, any missing textures will crash the game!");
            }
        } catch (IOException ex) {
            GGConsole.error("Failed to load the default texture, nonexistent textures may crash the program!");
        }
    }

    public static TextureData getTextureData(String name){
        return texturelist.getOrDefault(name, defaultdata);
    }
    
    public static TextureData getDefault(){
        return defaultdata;
    }
}
