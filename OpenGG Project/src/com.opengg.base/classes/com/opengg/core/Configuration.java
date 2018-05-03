/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Javier
 */
public final class Configuration{
    private static final Map<String,ConfigFile> settings = new HashMap<>();

    public static void load(File configfile) throws IOException{
        var properties = new Properties();

        properties.load(new FileInputStream(configfile.getAbsolutePath()));

        var propertyset = properties.entrySet();
        var datamap = new HashMap<String, String>();

        for(var entry : propertyset){
            datamap.put((String)entry.getKey(), (String)entry.getValue());
        }

        ConfigFile file = new ConfigFile(configfile.getName(), datamap);
        settings.put(configfile.getName(), file);
    }
    
    public static ConfigFile getConfigFile(String name){
        return settings.get(name);
    }
    
    public static String get(String key){
        return getEntry(key).getValue();
    }

    private static Entry<String, String> getEntry(String key){
        return settings.values().stream()
                //.filter(s -> s.name.equals(key.substring(0, key.indexOf('.'))))
                .flatMap(s -> s.getAllSettings().entrySet().stream())
                .filter(set -> set.getKey().equals(key))
                .findFirst()
                .get();
    }

    public static boolean set(String key, String val){
        var count = settings.values().stream()
                .filter(maps -> maps.getAllSettings().containsKey(key))
                .peek(maps -> maps.getAllSettings().replace(key, val))
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

    private Configuration() {
    }
}
