package com.opengg.core.script;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class ScriptLoader {
    private static URLClassLoader loader;

    public static void initialize() {
        try {
            loader = new ScriptClassLoader(new File(Resource.getAbsoluteFromLocal("resources\\scripts") + "\\").toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    private static Map<String, Script> scripts = new HashMap<>();

    public static Script getScriptByName(String name){
        if(scripts.containsKey(name)) return scripts.get(name);
        else return loadScript(name);
    }

    public static Script loadScript(String name){
        try {
            if(!new File(Resource.getAbsoluteFromLocal("resources\\scripts") + "\\" + name + ".class").exists()) throw new RuntimeException("Failed to find script named " + name);

            Class clazz = loader.loadClass(name);
            if(!clazz.getSuperclass().equals(Script.class)) throw new SecurityException("Attempted to load non-BiConsumer script!");
            var script = (Script) clazz.getConstructor().newInstance();
            GGConsole.log("Loaded script " + name);
            scripts.put(name,script);
            return script;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
