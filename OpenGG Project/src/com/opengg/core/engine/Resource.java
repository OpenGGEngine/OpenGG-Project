/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.audio.AudioLoader;
import com.opengg.core.audio.SoundData;
import com.opengg.core.audio.SoundManager;
import com.opengg.core.model.Model;
import com.opengg.core.model.ModelManager;
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
    
    public static String getAbsoluteFromLocal(String name){
        try {
            return new File(current, name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getModelPath(String name){
        return "resources\\models\\" +  name + "\\" + name + ".bmf";
    }
    
    public static String getConfigPath(String name){
        return "cfg\\" +  name + ".cfg";
    }
    
    public static String getShaderPath(String name){
        return "resources\\glsl\\" +  name;
    }
    
    public static String getTexturePath(String name){
        return "resources\\tex\\" +  name;
    }
    
    public static String getFontPath(String name){
        return "resources\\font\\" +  name + ".fnt";
    }
    
    public static String getSoundPath(String name){
        return "resources\\audio\\" +  name;
    }
    
    public static String getWorldPath(String name){
        return "resources\\worlds\\" +  name + ".bwf";
    }
    
    public static SoundData getSoundData(String name){
        return SoundManager.loadSound(getSoundPath(name));
    }
    
    public static Model getModel(String name){
        return ModelManager.loadModel(getModelPath(name));
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
