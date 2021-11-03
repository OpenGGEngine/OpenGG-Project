/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core;

import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author Javier
 */
public class GGInfo {
    private static UserDataOption userDataLocation = UserDataOption.DOCUMENTS;
    private static final String version = "0.1";
    private static String appName = "default";
    private static String userDataDirectory = "default";

    private static String memAllocator = "system";
    private static Path applicationRootPath;
    private static final boolean initialized = false;

    private static boolean redirectStandardOut = true;
    private static boolean verbose = false;
    private static boolean aggressiveMemory = false;
    private static boolean server = false;

    private static String glversion;
    private static int userId = -1;
    private static boolean menu = false;
    private static boolean ended = false;

    static{
        applicationRootPath = new File("").toPath().toAbsolutePath();
    }
    
    public static String getApplicationName() {
        return appName;
    }

    public static void setApplicationName(String appname) {
        GGInfo.appName = appname;
    }

    public static String getMemoryAllocator() {
        return memAllocator;
    }

    public static void setMemoryAllocator(String memallocator) {
        GGInfo.memAllocator = memallocator;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean isRedirectStandardOut() {
        return redirectStandardOut;
    }

    public static void setRedirectStandardOut(boolean redirectStandardOut) {
        GGInfo.redirectStandardOut = redirectStandardOut;
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

    public static boolean shouldAggressivelyManageMemory() {
        return aggressiveMemory;
    }

    public static void setAggressiveMemoryManagement(boolean agressiveMemory) {
        GGInfo.aggressiveMemory = agressiveMemory;
    }

    public static UserDataOption getUserDataLocation() {
        return userDataLocation;
    }

    public static void setUserDataLocation(UserDataOption userDataLocation) {
        GGInfo.userDataLocation = userDataLocation;
    }

    public static Path getUserDataPath(){
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            if(GGInfo.getUserDataLocation() == GGInfo.UserDataOption.DOCUMENTS) {
                return Path.of(System.getProperty("user.home"), "Documents", GGInfo.getUserDataDirectoryName());
            } else {
                return Path.of(System.getenv("APPDATA"), GGInfo.getUserDataDirectoryName());
            }
        } else {
            if(GGInfo.getUserDataLocation() == GGInfo.UserDataOption.DOCUMENTS) {
                return Path.of(System.getProperty("user.home"), ".local", "share", GGInfo.getUserDataDirectoryName());
            } else {
                return Path.of(System.getProperty("user.home"), "." + GGInfo.getUserDataDirectoryName());
            }
        }

    }


    public static String getUserDataDirectoryName() {
        return userDataDirectory;
    }

    public static void setUserDataDirectoryName(String userDataDirectory) {
        GGInfo.userDataDirectory = userDataDirectory;
    }

    public static Path getApplicationPath() {
        return applicationRootPath;
    }

    public static void setApplicationPath(Path basepath) {
        GGInfo.applicationRootPath = basepath;
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
