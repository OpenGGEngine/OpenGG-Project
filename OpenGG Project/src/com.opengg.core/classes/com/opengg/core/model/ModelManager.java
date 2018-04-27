/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.model;

import com.opengg.core.console.GGConsole;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Javier
 */
public class ModelManager{
    private static Map<String,Model> modellist = new HashMap<>();
    private static Model defaultm;
    
    public static void initialize(){
        
        try {
            defaultm = ModelLoader.loadModel("Resource/models/default/default.bmf");
        } catch (IOException ex) {
            GGConsole.error("Failed to load default model, nonexistent models may crash the program!");
        }
               
    }  
    
    public static void addModel( Model model){
        if(!modellist.containsKey(model.getName()))
            modellist.put(model.getName(), model);
    }
    
    public static Model getModel(String path){
        return modellist.get(path);
    }
    
    public static Model getDefaultModel(){
        return defaultm;
    }
    
    public static Model loadModel(String path){
        Model model = modellist.get(path);
        if(model != null)
            return model;
        try{
            model = ModelLoader.loadModel(path);
            if(model != null ){
                addModel(model);
                return model;
            }else{
                GGConsole.warn("Failed to load model at " + path + ", using default model instead");
            }
        }catch(Exception e){
            GGConsole.warn("Failed to load model at " + path + " due to " + e.toString() + ", using default model instead");
        }
        return defaultm;
    }
    
    public static Map<String,Model> getModelList(){
        return modellist;
    }
    
    public static void destroy(){
        for(Model model : modellist.values()){
            
        }
        modellist.clear();
    }
}
