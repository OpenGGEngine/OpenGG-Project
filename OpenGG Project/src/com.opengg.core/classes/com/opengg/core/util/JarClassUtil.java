/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import com.opengg.core.console.GGConsole;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Javier
 */
public class JarClassUtil {
    public static List<Class> loadAllClassesFromJar(String path){
        URLClassLoader loader;

        try {
            loader = new URLClassLoader(new URL[] {new URL("jar","", path)});
        }catch (MalformedURLException ex) {
            GGConsole.error("Failed to load classes due to malformed path: " + path);
            return new ArrayList<>();
        }
        
        List<Class> classes = new ArrayList<>();

        List<String> strings = loadClassnamesFromJar(path);

        try {
            loader.loadClass("com.opengg.core.engine.OpenGG");
        } catch (ClassNotFoundException ex) {
            System.out.println("IF THIS APPEARS IN YOUR CONSOLE MSG ME ON FACEBOOK CAUSE URLCLASSLOADER SUCKS");
        }

        for(String string : strings){
            if(string.contains("module-info")) continue;
            try{
                Class clazz = loader.loadClass(string);
                if(clazz.getCanonicalName() != null)
                    classes.add(clazz);
            } catch (ClassNotFoundException ex) {
                GGConsole.error("Failed to load class that was previously found: " + string);
            }
        }
        return classes;

    }
    
    public static List<String> loadClassnamesFromJar(String path){
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(path))){
            List<String> classNames = new ArrayList<>();
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    classNames.add(className.substring(0, className.length() - ".class".length()));
                }
            }   return classNames;
        } catch (IOException ex) {
            GGConsole.error("Failed to load classnames from " + path);
        } 
        return null;
    }
}
