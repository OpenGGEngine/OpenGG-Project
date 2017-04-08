/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Javier
 */
public class Config {
    static HashMap<String,ConfigFile> settings = new HashMap<>();
    
    public static void reloadConfigs(){
        File cfgdir = new File("config\\");
        File[] cfgs = cfgdir.listFiles();
        for(File f : cfgs){
            try {
                Properties p = new Properties();
                p.load(new FileInputStream(f.getAbsolutePath()));
                Set<Entry<Object,Object>> set = p.entrySet();
                HashMap<String,String> data = new HashMap<>();
                for(Entry<Object,Object> entry : set){
                    data.put((String)entry.getKey(), (String)entry.getValue());
                }
                ConfigFile file = new ConfigFile(f.getName(), data);
                settings.put(f.getName(), file);
            } catch (IOException ex) {
                GGConsole.warning("Failed to load config file at " + f.getAbsolutePath() + ", is it formatted?");
            }
        }
    }
    
    public static ConfigFile getConfigFile(String name){
        return settings.get(name);
    }
    
    public static String searchInFiles(String key){
        for(ConfigFile file : settings.values()){
            if(file.getAllSettings().containsKey(key))
                return file.getAllSettings().get(key);
        }
        return null;
    }
}
