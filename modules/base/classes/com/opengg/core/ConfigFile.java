/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Javier
 */
public class ConfigFile {
    private final String name;
    private final Map<String, String> contents;

    public ConfigFile(String name, Map<String, String> contents){
        this.name = name;
        this.contents = contents;
    }

    public String getName(){
        return name;
    }

    public String getConfig(String name){
        return contents.getOrDefault(name, "");
    }

    public void writeConfig(String name, String value) {
        contents.put(name, value);
    }
    
    public Map<String,String> getAllSettings(){
        return Map.copyOf(contents);
    }
}
