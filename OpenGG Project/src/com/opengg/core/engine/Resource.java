/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class Resource {
    public static String getLocal(String name){
        try {
            return new File(name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getModelPath(String name){
        try {
            return new File("resources\\models\\" +  name + "\\" + name + ".bmf").getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getConfigPath(String name){
        try {
            return new File("config\\" +  name + ".cfg").getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getShaderPath(String name){
        try {
            return new File("resources\\glsl\\" +  name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getTexturePath(String name){
        try {
            return new File("resources\\tex\\" +  name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getFontPath(String name){
        try {
            return new File("resources\\font\\" +  name + ".fnt").getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static String getSoundPath(String name){
        try {
            return new File("resources\\audio\\" +  name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
}
