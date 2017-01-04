/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.model;

import com.opengg.core.engine.GGConsole;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Javier
 */
public class ModelManager {

    private static Map<String,Model> modellist = new HashMap<>();
    public static int repsave = 0;
    public static String defPath;
    public static void initialize(){
        try {
            File deft = new File("resources/models/default/default.bmf");
            if(deft.exists()){
                defPath = deft.getCanonicalPath();
                Model def = ModelLoader.loadModel(defPath);
                modellist.put("default", def);
            }else{
                throw new IOException();
            }
        } catch (IOException ex) {
            GGConsole.error("Failed to load default model, nonexistent models may crash the program!");
        }
    }
    public static Model getModel(String path){
        return modellist.get(path);
    }
    public static void setModel(String path, Model t){
        if(modellist.containsKey(path)){
            modellist.replace(path, t);
        }else{
            modellist.put(path, t);
        }
    }
    public static int numModels(){
        return modellist.size();
    }
    public static void releaseModel(String s){
        modellist.remove(s);
    }
    public static void destroy(){
        modellist.values().stream().forEach((t) -> {
        });
        modellist.clear();
    }
}
