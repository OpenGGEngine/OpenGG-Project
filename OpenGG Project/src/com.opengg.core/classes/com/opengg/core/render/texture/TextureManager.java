/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Warren
 */
public class TextureManager {
    private static final Map<String, TextureData> texturelist = new HashMap<>();
    private static TextureData defaultdata;
    private static boolean cache = true;
    
    public static void initialize(){
        try {
            defaultdata = TextureLoader.loadTexture(Resource.getTexturePath("default.png"));
            GGConsole.log("Loaded default texture at " + defaultdata.source);
        } catch (IOException ex) {
            GGConsole.error("Failed to load the default texture, nonexistent textures may crash the program!");
        }
    }
    
    public static void addTexture(TextureData data){
        if(!cache) return;
        texturelist.put(data.source, data);
    }

    public static TextureData getTextureData(String name){
        return texturelist.getOrDefault(name, defaultdata);
    }
    
    public static TextureData getDefault(){
        defaultdata.buffer.rewind();
        return defaultdata;
    }
    
    public static Map<String, TextureData> getData(){
        return Collections.unmodifiableMap(texturelist);
    }
    
    public static void unloadTexture(String path){
        texturelist.remove(path);
    }
    
    public static TextureData loadTexture(String path){
        return loadTexture(path, true);
    }
    
    public static TextureData loadTexture(String path, boolean flip){
        if(texturelist.get(path) != null){
            TextureData data = texturelist.get(path);
            data.buffer.rewind();
            return data;
        }

        try{
            TextureData data = TextureLoader.loadTexture(path, flip);
            addTexture(data);
            return data;
        }catch(IOException e){
            GGConsole.warn("Failed to load texture at " + path + ", using default instead");
            return defaultdata;
        }catch(Exception e){
            throw new RuntimeException("Exception while loading texture " + path, e);
        }
    }
    
    public static void destroy(){

    }

    private TextureManager() {
    }
}
