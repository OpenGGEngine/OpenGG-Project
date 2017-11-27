/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import java.util.HashMap;

/**
 *
 * @author Javier
 */
public class ConfigFile {
    String name;
    HashMap<String, String> contents;
    
    public ConfigFile(String name, HashMap<String, String> contents){
        this.name = name;
        this.contents = contents;
    }
    
    public String getConfig(String name){
        return contents.get(name);
    }
    
    public HashMap<String,String> getAllSettings(){
        HashMap copy = new HashMap();
        for(String id : contents.keySet()){
            copy.put(id, contents.get(id));
        }
        return copy;
    }
}
