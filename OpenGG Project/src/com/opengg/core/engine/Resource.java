/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

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
        return "resources" + File.separator + "models" + File.separator + name + File.separator + name + ".bmf";
    }
    
    public static String getConfigPath(String name){
        return "cfg" + File.separator + name + ".cfg";
    }
    
    public static String getShaderPath(String name){
        return "resources" + File.separator + "glsl" + File.separator +  name;
    }
    
    public static String getTexturePath(String name){
        return "resources" + File.separator + "tex" + File.separator +  name;
    }
    
    public static String getFontPath(String name){
        return "resources" + File.separator + "font" + File.separator +  name + ".fnt";
    }
    
    public static String getSoundPath(String name){
        return "resources" + File.separator + "audio" + File.separator +  name;
    }
    
    public static String getWorldPath(String name){
        return "resources" + File.separator + "worlds" + File.separator +  name + ".bwf";
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
