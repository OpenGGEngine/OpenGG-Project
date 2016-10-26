/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Warren
 */
public class TextureManager {
    private static Map<String,Texture> texturelist = new HashMap<>();
    public static int repsave = 0;
    public static Texture getTexture(String path){
        Texture x = texturelist.get(path);
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
        texturelist.clear();
    }
}
