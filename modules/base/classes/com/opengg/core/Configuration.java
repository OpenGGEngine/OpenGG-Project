/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Javier
 */
public final class Configuration {
    private static final Map<String, ConfigFile> files = new HashMap<>();

    private Configuration() {
    }

    public static void load(File file) throws IOException {
        files.put(file.getName(), new ConfigFile(file));
    }

    public static void addConfigFile(ConfigFile file) {
        files.put(file.getName(), file);
    }

    public static ConfigFile getConfigFile(String name) {
        return files.get(name);
    }

    public static String get(String key) {
        return files.values().stream()
                .map(k -> k.getConfig(key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("");
    }

    public static boolean set(String key, String val) {
        var count = files.values().stream()
                .filter(maps -> maps.getAllSettings().containsKey(key))
                .peek(maps -> maps.writeConfig(key, val))
                .count();

        return count > 0;
    }

    public static float getFloat(String key) {
        var result = get(key);
        try {
            return Float.parseFloat(result);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean getBoolean(String key) {
        var result = get(key);
        try {
            return Boolean.parseBoolean(result);
        } catch (Exception e) {
            return false;
        }
    }

    public static int getInt(String key) {
        var result = get(key);
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void writeFile(ConfigFile file) throws IOException {
        Properties prop = new Properties();
        file.getAllSettings().forEach(prop::put);

        FileOutputStream fos;
        if (GGInfo.getUserDataLocation() == GGInfo.UserDataOption.DOCUMENTS)
            fos = new FileOutputStream(System.getProperty("user.home") + File.separator + "Documents"
                    + File.separator + GGInfo.getUserDataDirectory() + File.separator + "config" + File.separator + file.getName());
        else
            fos = new FileOutputStream(System.getenv("APPDATA") +
                    File.separator + GGInfo.getUserDataDirectory() + File.separator + "config" + File.separator + file.getName());


        prop.store(fos, "");
        fos.close();
    }
}
