/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.audio;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for all loaded SoundData objects 
 * @author Javier
 */
public class SoundManager {
    /**
     * HashMap containing all sounds indexed with their sources, to prevent double loading
     */
    static HashMap<String, SoundData> sounds = new HashMap<>();
    
    /**
     * Default data used if a loaded one is missing
     */
    static SoundData defaultdata;
    
    public static void initialize(){
        try {
            defaultdata = AudioLoader.loadVorbis(Resource.getSoundPath("default.ogg"));
        } catch (IOException ex) {
            GGConsole.error("Failed to load the default sound, nonexistent sound files may crash the program!");
        }
    }
    
    /**
     * Adds a {@link SoundData} object to the sound HashMap
     * @param data Data to insert
     */
    public static void addSoundData(SoundData data){
        sounds.put(data.origin, data);
    }
    
    /**
     * Returns the {@link SoundData} associated with a given path, or the default SoundData if the path cannot be found in the map
     * @param path Path to search for in the sounds map
     * @return SoundData object associated with the path, or the default if unavailable
     */
    public static SoundData getSoundData(String path){
        return sounds.getOrDefault(path, defaultdata);
    }
    
    /**
     * Gets the default sound
     * @return Default sound
     */
    public static SoundData getDefault(){
        defaultdata.data.rewind();
        return defaultdata;
    }
    
    /**
     * Gets the full HashMap in this manager
     * @return Full map
     */
    public static Map<String, SoundData> getData(){
        return sounds;
    }
    
    /**
     * Loads sound from the given path, unless it can be found in the map in which case that will be returned instead
     * @param path Path to file
     * @return SoundData with given path, either from the file or from the map
     */
    public static SoundData loadSound(String path){
        if(sounds.get(path) != null)
            return sounds.get(path);
        try{
            SoundData data = AudioLoader.loadVorbis(path);
            addSoundData(data);
            return data;
        }catch(IOException e){
            GGConsole.warn("Failed to load sound at " + path + ", using default instead");
            return defaultdata;
        }
    }       

    private SoundManager() {
    }
}
