/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import com.opengg.core.console.GGConsole;
import com.opengg.core.world.Deserializer;
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
 *
 * @author Javier
 */
public class JarClassUtil {
    public static List<Class> loadAllClassesFromJar(String path) {
        JarFile jarFile;
        try {
            jarFile = new JarFile(path);
        } catch (IOException ex) {
            GGConsole.error("Failed to find jarfile!");
            throw new RuntimeException(ex);
        }
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls;
        try {
            urls = new URL[]{new URL("jar:file:" + path + "!/")};
        } catch (MalformedURLException ex) {
            GGConsole.error("Failed to access jarfile!");
            throw new RuntimeException(ex);
        }
        URLClassLoader cl = URLClassLoader.newInstance(urls);
        Deserializer.loaders.add(cl);
        List<Class> classes = new ArrayList<>();
        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }
            String className = je.getName().substring(0, je.getName().length() - ".class".length());
            className = className.replace('/', '.');
            if(className.contains("module-info")) continue;
            if(className.contains("com.opengg.core")) continue;
            if(className.contains("com.opengg.math")) continue;
            if(className.contains("com.opengg.base")) continue;
            if(className.contains("com.opengg.system")) continue;
            if(className.contains("com.opengg.console")) continue;

            try {
                Class clazz = cl.loadClass(className);
                classes.add(clazz);
            } catch (ClassNotFoundException ex) {
                GGConsole.error("Failed to load classes!");
                throw new RuntimeException(ex);
            }

        }
        return classes;
    }

    public static List<Class> getAllClassesFromJar(String path) {
        JarFile jarFile;
        try {
            jarFile = new JarFile(path);
        } catch (IOException ex) {
            GGConsole.error("Failed to find jarfile!");
            throw new RuntimeException(ex);
        }
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls;
        try {
            urls = new URL[]{new URL("jar:file:" + path + "!/")};
        } catch (MalformedURLException ex) {
            GGConsole.error("Failed to access jarfile!");
            throw new RuntimeException(ex);
        }
        URLClassLoader cl = URLClassLoader.newInstance(urls);
        List<Class> classes = new ArrayList<>();
        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }
            String className = je.getName().substring(0, je.getName().length() - ".class".length());
            className = className.replace('/', '.');
            if(className.contains("module-info")) continue;
            if(className.contains("com.opengg.core")) continue;
            if(className.contains("com.opengg.math")) continue;
            if(className.contains("com.opengg.base")) continue;
            if(className.contains("com.opengg.system")) continue;
            if(className.contains("com.opengg.console")) continue;

            try {
                Class clazz = cl.loadClass(className);
                classes.add(clazz);
            } catch (ClassNotFoundException ex) {
                GGConsole.error("Failed to load classes!");
                throw new RuntimeException(ex);
            }

        }
        return classes;
    }

    private JarClassUtil() {
    }

}
