/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Javier
 */
public class ConfigFile {
    private String name;
    private HashMap<String, String> contents;
    
    public ConfigFile(String name, HashMap<String, String> contents){
        this.name = name;
        this.contents = contents;
    }
    
    public String getConfig(String name){
        return contents.get(name);
    }
    
    public Map<String,String> getAllSettings(){
        Map copy = new HashMap<String, String>();
        for(String id : contents.keySet()){
            copy.put(id, contents.get(id));
        }
        return copy;
    }
}
