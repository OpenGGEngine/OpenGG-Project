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
    private static UserDataOption userDataLocation = UserDataOption.DOCUMENTS;
    private static final String version = "0.1";
    private static String appname = "default";
    private static String memallocator = "system";
    private static String basepath = "";
    private static boolean initialized = false;
    private static boolean verbose = false;
    private static boolean agressiveMemory = false;
    private static boolean server = false;
    private static String glversion;
    private static int userId = -1;
    private static boolean menu = false;
    private static boolean ended = false;

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

    public static void setApplicationName(String appname) {
        GGInfo.appname = appname;
    }

    public static String getMemoryAllocator() {
        return memallocator;
    }

    public static void setMemoryAllocator(String memallocator) {
        GGInfo.memallocator = memallocator;
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

    public static void setGlVersion(String glversion) {
        GGInfo.glversion = glversion;
    }

    public static void setVerbose(boolean verb){
        verbose = verb;
    }

    public static boolean shouldAgressivelyManageMemory() {
        return agressiveMemory;
    }

    public static UserDataOption getUserDataLocation() {
        return userDataLocation;
    }

    public static void setUserDataLocation(UserDataOption userDataLocation) {
        GGInfo.userDataLocation = userDataLocation;
    }

    public static void setAgressiveMemoryManagement(boolean agressiveMemory) {
        GGInfo.agressiveMemory = agressiveMemory;
    }

    public static String getApplicationPath() {
        return basepath;
    }

    public static void setApplicationPath(String basepath) {
        GGInfo.basepath = basepath;
    }

    public static int getUserId(){
        return userId;
    }

    public static void setUserId(int userId){
        GGInfo.userId = userId;
    }

    public static boolean isMenu() {
        return menu;
    }

    public static void setMenu(boolean menu) {
        GGInfo.menu = menu;
    }

    public static boolean isEnded() {
        return ended;
    }

    public static void setEnded(boolean ended) {
        GGInfo.ended = ended;
    }

    private GGInfo() {
    }

    public enum UserDataOption{
        DOCUMENTS, APP_DATA
    }
    
}
