/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Javier
 */
public class ConfigFile {
    private final String name;
    private Map<String, String> contents;

    public ConfigFile(Path file) throws IOException {
        var properties = new Properties();

        properties.load(new FileInputStream(file.toFile()));

        this.name = file.getFileName().toString();
        contents = properties.isEmpty() ? new LinkedHashMap<>() : new LinkedHashMap<>(Map.ofEntries(properties.entrySet().toArray(new Map.Entry[1])));
    }

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
