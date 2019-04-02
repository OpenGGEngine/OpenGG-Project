
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.Configuration;
import com.opengg.core.GGInfo;
import com.opengg.core.animation.AnimationManager;
import com.opengg.core.audio.SoundEngine;
import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.console.GGConsole;
import com.opengg.core.extension.Extension;
import com.opengg.core.extension.ExtensionManager;
import static com.opengg.core.render.window.RenderUtil.endFrame;
import static com.opengg.core.render.window.RenderUtil.startFrame;

import com.opengg.core.gui.GUIController;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.physics.PhysicsEngine;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.system.Allocator;
import com.opengg.core.system.SystemInfo;
import com.opengg.core.thread.ThreadManager;
import com.opengg.core.util.Time;
import com.opengg.core.world.WorldEngine;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Primary controller of all functionality in the OpenGG Engine
 * @author Javier
 */
public final class OpenGG{
    private static GGApplication app;
    private static Instant startTime;
    private static boolean head = false;
    private static boolean force = false;
    private static boolean verbose = false;
    private static boolean test = false;
    private static Time time;
    private static Thread mainthread;
    private static float targetUpdate = 0f;
    private static float overrideUpdate = -1f;

    private OpenGG(){}

    /**
     * Initializes the OpenGG Engine. This gives full runtime control of the thread to OpenGG,
     * so no code will run past this call until the engine closes
     * @param app Instance of the OpenGG-driven application
     * @param info Window information
     */
    public static void initialize(GGApplication app, WindowInfo info){
        try{
            if(info == null){
                initializeLocal(app, null, false);
            }else{
                initializeLocal(app, info, true);
            }
        }catch(Exception e){
            GGConsole.error("Uncaught exception: " + e.getMessage());
            e.printStackTrace();
            try {Thread.sleep(10);} catch (InterruptedException ex) {}
            closeEngine();
            writeErrorLog();
            System.exit(0);
        }

    }

    public static void initializeHeadless(GGApplication app){
        initialize(app, null);
    }

    private static void initializeClient(WindowInfo windowinfo){
        WindowController.setup(windowinfo);
        GGConsole.log("Window generation successful, using OpenGL context version " + RenderEngine.getGLVersion());

        SystemInfo.queryOpenGLInfo();
        GGConsole.log("OpenGL instance info acquired");

        RenderEngine.initialize();
        GGGameConsole.initialize();
        GGDebugRenderer.initialize();
        SoundEngine.initialize();

        BindController.initialize();
        GGConsole.log("Bind Controller initialized");

        ExtensionManager.loadStep(Extension.GRAPHICS);
    }

    private static void initializeServer(){
        RenderEngine.initializeForHeadless();
        GGConsole.log("Render Engine headless initialization complete");
    }

    private static void initializeLocal(GGApplication ggapp, WindowInfo info, boolean client){
        time = new Time();
        startTime = Instant.now();
        mainthread = Thread.currentThread();
        head = client;
        app = ggapp;

        GGInfo.setServer(!client);
        ThreadManager.initialize();
        Executor.initialize();
        GGConsole.initialize();
        GGConsole.addListener(new OpenGGCommandExtender());
        GGConsole.log("OpenGG initializing, running on " + System.getProperty("os.name") + ", " + System.getProperty("os.arch"));

        Resource.initialize();
        GGConsole.log("Resource system initialized");

        getVMOptions();

        ExtensionManager.loadStep(Extension.NONE);

        ExtensionManager.loadStep(Extension.LWJGL);

        SystemInfo.querySystemInfo();
        OpenGG.loadConfigs();
        GGConsole.log("Loaded configuration files");

        ExtensionManager.loadStep(Extension.CONFIG);

        WorldEngine.initialize();
        GGConsole.log("World Engine initialized");

        PhysicsEngine.initialize();
        GGConsole.log("Physics Engine initialized");

        if(client)
            initializeClient(info);
        else
            initializeServer();

        GGConsole.log("Engine initialization complete, application setup beginning");
        app.setup();

        GGConsole.log("Application setup complete");
        GGConsole.log("OpenGG initialized in " + time.getDeltaMs() + " milliseconds");

        if(client)
            run();
        else
            runHeadless();
    }

    private static void runHeadless(){
        while(!GGInfo.isEnded()){
            runUpdate();
        }

        GGConsole.log("OpenGG closing...");
        GGInfo.setEnded(true);
        if(!force){
            closeEngine();
        }
        writeLog();
    }

    private static void run(){
        while (!getWindow().shouldClose() && !GGInfo.isEnded()) {
            runUpdate();
            runInput();
            runRender();
        }

        GGConsole.log("OpenGG closing...");
        GGInfo.setEnded(true);
        if(!force){
            closeEngine();
        }
        writeLog();
    }

    private static void runUpdate() {
        float delta = time.getDeltaSec();
        if(delta < targetUpdate) {
            try {
                Thread.sleep((long) ((targetUpdate-delta)*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        delta = overrideUpdate > 0f ? overrideUpdate : delta;

        Allocator.update();
        GGDebugRenderer.update(delta);
        Executor.getExecutor().update(delta);
        ExtensionManager.update(delta);
        WorldEngine.update(delta);
        GUIController.update(delta);
        AnimationManager.update(delta);
        PhysicsEngine.updatePhysics(delta);
        getApp().update(delta);
        SoundtrackHandler.update();
        NetworkEngine.update();
    }

    private static void runInput() {
        MouseController.update();
    }

    private static void runRender() {
        WindowController.update();
        startFrame();
        getApp().render();
        ExtensionManager.render();
        RenderEngine.render();
        RenderEngine.checkForGLErrors();
        endFrame();
    }

    private static void getVMOptions(){
        String verb = System.getProperty("gg.verbose");
        String stest = System.getProperty("gg.istest");
        if(verb != null)
            if(verb.equals("true"))
                GGInfo.setVerbose(true);

        if(stest != null)
            if(stest.equals("true"))
                test = true;
    }

    private static void loadConfigs(){
        var configdir = new File(Resource.getAbsoluteFromLocal("config/"));
        var allconfigs = recursiveLoadConfigs(configdir);
        for(var config : allconfigs){
            try{
                Configuration.load(config);
            }catch(IOException e){
                GGConsole.error("Failed to load configuration file at " + config.getAbsolutePath());
            }
        }
    }

    private static List<File> recursiveLoadConfigs(File directory){
        var allfiles = directory.listFiles();
        var allcfgs = new ArrayList<File>();
        for (var file : allfiles) {
            if (file.isFile()) {
                if(file.getAbsolutePath().contains(".ini")){
                    allcfgs.add(file);
                }
            } else if(file.isDirectory()) {
                recursiveLoadConfigs(file);
            }
        }
        return allcfgs;
    }

    /**
     * Marks the current instance of OpenGG to end safely on the next update cycle, will run all cleanup code
     */
    public static void endApplication(){
        GGConsole.log("Application end has been requested");
        GGInfo.setEnded(true);
    }

    /**
     * Force ends the application immediately.<br>
     * Because it forces the application, including all other threads, to end (basically calling {@code System.exit(0)}),
     * it does not cleanup Resource currently in use.
     * Only use in extreme circumstances (like in case of a program freeze)
     */
    public static void forceEnd(){
        GGConsole.warning("Application is force quitting");
        force = true;
        GGInfo.setEnded(true);
        System.exit(0);
    }

    /**
     * Returns the current {@link com.opengg.core.render.window.Window} for this instance
     * @return The current window
     * @see WindowController#getWindow()
     */
    public static Window getWindow() {
        return WindowController.getWindow();
    }

    /**
     * Returns the current {@link GGApplication} for this instance
     * @return The current GGApplication
     */
    public static GGApplication getApp() {
        return app;
    }

    /**
     * Returns if the application is marked to end or has ended
     * @return If marked or actually has ended
     */
    public static boolean getEnded(){
        return !GGInfo.isEnded();
    }

    /**
     * Returns if the logging setting is verbose
     * @return If the logging is set to verbose
     */
    public static boolean isVerbose() {
        return verbose;
    }

    private static void closeEngine(){
        RenderEngine.destroy();
        SoundEngine.destroy();
        GGConsole.log("Audio controller has been finalized");
        WindowController.destroy();
        ThreadManager.destroy();
        GGConsole.log("Thread Manager has closed all remaining threads");
        GGConsole.log("OpenGG has closed gracefully, application can now be ended");
    }

    private static void writeLog(){
        if(test) {
        }
        //GGConsole.writeLog(startTime);
    }

    private static void writeErrorLog(){
        if(test) return;
        String error = SystemInfo.getInfo();
    }

    public static float getLastTickLength(){
        return GGDebugRenderer.getLastFrameTime();
    }

    /**
     * Returns the starting time for this OpenGG instance, starting from the initial call to OpenGG.initialize()
     * @return Start time
     */
    public static Instant getStartTime() {
        return startTime;
    }

    /**
     * Returns if the thread in which this method is called is the main thread
     * @return If is in main thread
     */
    public static boolean inMainThread(){
        return mainthread == Thread.currentThread();
    }

    /**
     * Gives the engine the given {@link Runnable} to run in the next cycle.<br>
     * This functionality is useful to be able to run functions that require the main thread (For example requiring OpenGL calls)
     * @param e Overrided Runnable to be run
     */
    public static Executor.Sleeper asyncExec(Runnable e){
        return Executor.async(e);
    }

    /**
     * Gives the engine the given {@link Runnable} to run in the amount of seconds given with one cycle length deviation
     * @param seconds In how many seconds to run the Runnable
     * @param e Overrided Runnable to be run
     */
    public static Executor.Sleeper asyncExec(float seconds, Runnable e){
        return Executor.in(seconds, e);
    }

    /**
     * Gives the engine the given {@link Runnable} to run in the next cycle.<br>
     * This functionality is useful to be able to run functions that require the main thread (For example requiring OpenGL calls).<br>
     * This version blocks the current thread until the Runnable is run
     * @param e Overrided Runnable to be run
     */
    public static void syncExec(Runnable e){
       Executor.sync(e);
    }

    public static void setTargetUpdateTime(float time) {
        OpenGG.targetUpdate = time;
    }

    public static void setOverrideWorldUpdateTime(float time) {
        OpenGG.overrideUpdate = time;
    }
}
