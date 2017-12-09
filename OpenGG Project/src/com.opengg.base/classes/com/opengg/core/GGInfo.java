/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core;

/**
 *
 * @author Javier
 */
public class GGInfo {
    private static String appname = "default";
    private static String memallocator = "system";
    private static boolean initialized = false;
    private static String version = "0.0.1";
    private static boolean verbose = false;
    private static String glversion = "4.2";

    public static String getApplicationName() {
        return appname;
    }

    public static String getMemoryAllocator() {
        return memallocator;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static String getVersion() {
        return version;
    }

    public static boolean isVerbose() {
        return verbose;
    }
    
    public static String getGlVersion(){
        return glversion;
    }
}
