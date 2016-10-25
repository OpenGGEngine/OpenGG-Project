/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture;

import com.opengg.core.util.GlobalUtil;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Warren
 */
public class TextureManager {
    private static Map<String,Texture> texturelist = new HashMap<>();
    public static int repsave = 0;
    public static void loadTexture(String path,boolean flipped){
        repsave++;
        if(!texturelist.containsKey(path)){
            Texture dumb = new Texture();
            dumb.loadTexture(path, flipped);
            texturelist.put(path, dumb);
        }
    }
    public static Texture getTexture(String path){
        Texture x = texturelist.get(path);
        if(x == null){
            GlobalUtil.error("Texture: " + path + "does not exist or has not been loaded!");
            return Texture.blank;
        }
        return x;
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
