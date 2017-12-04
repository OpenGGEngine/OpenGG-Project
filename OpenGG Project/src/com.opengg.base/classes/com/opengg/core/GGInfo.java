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
    private static String appname;
    private static String memallocator;
    private static boolean initialized;
    private static String version;
    private static boolean verbose;
    private static String glversion;

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
