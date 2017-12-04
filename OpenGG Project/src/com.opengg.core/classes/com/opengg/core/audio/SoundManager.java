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
 *
 * @author Javier
 */
public class SoundManager {
    static HashMap<String, SoundData> sounds = new HashMap<>();
    static SoundData defaultdata;
    
    public static void initialize(){
        try {
            defaultdata = AudioLoader.loadVorbis(Resource.getSoundPath("default.ogg"));
        } catch (IOException ex) {
            GGConsole.error("Failed to load the default sound, nonexistent sound files may crash the program!");
        }
    }
    
    public static void addSoundData(SoundData data){
        sounds.put(data.origin, data);
    }
    
    public static SoundData getSoundData(String path){
        return sounds.getOrDefault(path, defaultdata);
    }
    
    public static SoundData getDefault(){
        defaultdata.data.rewind();
        return defaultdata;
    }
    
    public static Map<String, SoundData> getData(){
        return sounds;
    }
    
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
}
