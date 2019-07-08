/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import com.opengg.core.console.GGConsole;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Javier
 */
public class JarClassUtil {
    private static EditableURLClassLoader loader = new EditableURLClassLoader(new URL[0]);

    private JarClassUtil() {
    }

    public static List<Class> loadAllClassesFromJar(String path) {
        return loadInternal(path);
    }

    private static List<Class> loadInternal(String path) {
        try {
            Enumeration<JarEntry> files = new JarFile(path).entries();

            loader.addURL(new URL("jar:file:" + path + "!/"));
            List<Class> classes = new ArrayList<>();
            while (files.hasMoreElements()) {
                JarEntry je = files.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }
                String className = je.getName().substring(0, je.getName().length() - ".class".length()).replace('/', '.');
                if (className.contains("module-info")) continue;
                if (className.contains("com.opengg.core")) continue;
                if (className.contains("com.opengg.math")) continue;
                if (className.contains("com.opengg.base")) continue;
                if (className.contains("com.opengg.system")) continue;
                if (className.contains("com.opengg.console")) continue;


                Class clazz = Class.forName(className, false, loader);
                classes.add(clazz);
            }

            return classes;
        } catch (MalformedURLException ex) {
            GGConsole.error("Failed to access jarfile!");
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            GGConsole.error("Failed to findByName jarfile!");
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            GGConsole.error("Failed to load classes!");
            throw new RuntimeException(ex);
        }
    }

    public static EditableURLClassLoader getLoader(){
        return loader;
    }
}
