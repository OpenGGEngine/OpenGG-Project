/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 *
 * @author Javier
 */
public final class Configuration{
    private static final Map<String,ConfigFile> settings = new HashMap<>();

    public static void load(File file) throws IOException{
        settings.put(file.getName(), new ConfigFile(file));
    }

    public static void addConfigFile(ConfigFile file){
        settings.put(file.getName(),file);
    }
    
    public static ConfigFile getConfigFile(String name){
        return settings.get(name);
    }
    
    public static String get(String key){
        return settings.values().stream()
                .filter(k -> k.getAllSettings().containsKey(key))
                .map(k -> k.getConfig(key))
                .findFirst()
                .orElse("");
    }

    public static boolean set(String key, String val){
        var count = settings.values().stream()
                .filter(maps -> maps.getAllSettings().containsKey(key))
                .peek(maps -> maps.writeConfig(key, val))
                .count();

        return count > 0;
    }

    public static float getFloat(String key){
        var result = get(key);
        try{
            return Float.parseFloat(result);
        }catch (Exception e){
            return 0;
        }
    }

    public static int getInt(String key){
        var result = get(key);
        try{
            return Integer.parseInt(result);
        }catch (Exception e){
            return 0;
        }
    }
    
    public static void writeConfig(ConfigFile file) throws IOException {
        Properties prop = new Properties();
        file.getAllSettings().forEach(prop::put);
        FileOutputStream fos = new FileOutputStream("config" + File.separator + file.getName());

        prop.store(fos,"");
        fos.close();
    }

    private Configuration() {
    }
}
