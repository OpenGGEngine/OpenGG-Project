/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.audio.AudioLoader;
import com.opengg.core.audio.SoundData;
import com.opengg.core.model.Model;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.text.GGFont;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class Resource {
    static String current;
    
    public static void initialize(){
        try{
            current = new File("").getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.error("Failed to get default path!");
        }
    }
    
    public static String getLocal(String name){
        try {
            return new File(current, name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getModelPath(String name){
        try {
            return new File(current, "resources\\models\\" +  name + "\\" + name + ".bmf").getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getConfigPath(String name){
        try {
            return new File(current, "config\\" +  name + ".cfg").getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getShaderPath(String name){
        try {
            return new File(current, "resources\\glsl\\" +  name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getTexturePath(String name){
        try {
            return new File(current, "resources\\tex\\" +  name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getFontPath(String name){
        try {
            return new File(current, "resources\\font\\" +  name + ".fnt").getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getSoundPath(String name){
        try {
            return new File(current, "resources\\audio\\" +  name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static SoundData getSoundData(String name){
        return AudioLoader.loadVorbis(getSoundPath(name));
    }
    
    public static Model getModel(String name){
        return ModelLoader.loadModel(getModelPath(name));
    }
    
    public static Texture getTexture(String name){
        return Texture.get2DTexture(getTexturePath(name));
    }
    
    public static GGFont getFont(String fname, String ftexname){
        String fpath = getFontPath(fname);
        String tpath = getTexturePath(ftexname);
        return new GGFont(tpath,fpath);
    }
    
    public static void setDefaultPath(String path){
        current = path;
    }
}
