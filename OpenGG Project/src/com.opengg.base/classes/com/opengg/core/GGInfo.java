/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class GGInfo {
    private static final String version = "0.1";
    private static String appname = "default";
    private static String memallocator = "system";
    private static String basepath = "";
    private static boolean initialized = false;
    private static boolean verbose = false;
    private static boolean agressiveMemory = false;
    private static boolean server = false;
    private static String glversion = "4.2";
    private static int userId = 0;

    static{ 
        try {
            basepath = new File("").getCanonicalPath();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
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

    public static boolean isServer() {
        return server;
    }

    public static void setServer(boolean server) {
        GGInfo.server = server;
    }

    public static String getGlVersion(){
        return glversion;
    }
    
    public static void setVerbose(boolean verb){
        verbose = verb;
    }

    public static boolean shouldAgressivelyManageMemory() {
        return agressiveMemory;
    }

    public static void setAgressiveMemoryManagement(boolean agressiveMemory) {
        GGInfo.agressiveMemory = agressiveMemory;
    }

    public static String getApplicationPath() {
        return basepath;
    }

    public static int getUserId(){
        return userId;
    }

    public static void setUserId(int userId){
        GGInfo.userId = userId;
    }

    private GGInfo() {
    }
    
    
}
