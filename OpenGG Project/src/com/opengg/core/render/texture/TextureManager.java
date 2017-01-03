/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.engine.GGConsole;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Warren
 */
public class TextureManager {
    private static Map<String,Texture> texturelist = new HashMap<>();
    public static int repsave = 0;
    public static String defPath;
    public static void initialize(){
        try {
            File deft = new File("resources/tex/default.png");
            if(deft.exists()){
                defPath = deft.getCanonicalPath();
                Texture def = new Texture(defPath);
                texturelist.put("default", def);
            }else{
                throw new IOException();
            }
        } catch (IOException ex) {
            GGConsole.error("Failed to load default texture, nonexistent textures may crash the program!");
        }
    }
    public static Texture getTexture(String path){
        Texture x = texturelist.get(path);
        if(x == null) x = new Texture(path);
        return x;
    }
    public static void setTexture(String path, Texture t){
        if(texturelist.containsKey(path)){
            texturelist.replace(path, t);
        }else{
            texturelist.put(path, t);
        }
    }
    public static int numTextures(){
        return texturelist.size();
    }
    public static void releaseTexture(String s){
        texturelist.remove(s);
    }
    public static void destroy(){
        texturelist.values().stream().forEach((t) -> {
            t.destroy();
        });
        texturelist.clear();
    }
}
