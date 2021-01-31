
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

import com.opengg.core.gui.GUIController;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.physics.PhysicsEngine;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.script.ScriptLoader;
import com.opengg.core.system.Allocator;
import com.opengg.core.system.NativeResourceManager;
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
    private static InitializationOptions initOptions;
    private static Instant startTime;
    private static boolean force = false;
    private static boolean test = false;
    private static boolean render = true;
    private static Time time;
    private static Thread mainThread;
    private static final DebugOptions options = new DebugOptions();
    private static float targetUpdate = 0f;
    private static float overrideUpdate = -1f;

    private OpenGG(){}

    /**
     * Initializes the OpenGG Engine. This gives full runtime control of the thread to OpenGG,
     * so no code will run past this call until the engine closes
     * @param app Instance of the OpenGG-driven application
     * @param options Initialization information
     */
    public static void initialize(GGApplication app, InitializationOptions options){
        try{
            initializeLocal(app, options, !options.isHeadless());
        }catch(Exception e){
            GGConsole.error("Uncaught exception: " + e.getMessage());
            e.printStackTrace();
            try {Thread.sleep(10);} catch (InterruptedException ex) {}
            writeUserData();
            closeEngine();
            System.exit(0);
        }
    }

    private static void initializeClient(WindowOptions windowOptions){
        WindowController.setup(windowOptions);

        SystemInfo.queryRendererInfo("OpenGL");
        GGConsole.log("Graphics backend created");
        GGConsole.log("Running renderer on " + SystemInfo.get("Graphics Renderer") + " provided by " + SystemInfo.get("Graphics Vendor"));

        RenderEngine.initialize(windowOptions);

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

    private static void initializeLocal(GGApplication ggapp, InitializationOptions options, boolean client){
        time = new Time();
        startTime = Instant.now();
        mainThread = Thread.currentThread();
        initOptions = options;
        app = ggapp;

        GGInfo.setServer(!client);
        GGInfo.setApplicationName(options.getApplicationName());

        ThreadManager.initialize();
        Executor.initialize();
        GGConsole.initialize();
        GGConsole.addListener(new OpenGGCommandExtender());
        GGConsole.log("OpenGG initializing, running on " + System.getProperty("os.name") + ", " + System.getProperty("os.arch"));
        GGConsole.log("Initializing application " + options.getApplicationName() + " with app ID " + options.getApplicationId());


        //Policy.setPolicy(new OpenGGSecurityPolicy());
        //System.setSecurityManager(new SecurityManager());

        GGConsole.log("Switched runtime security to OpenGG security policy");

        Resource.initialize();
        GGConsole.log("Resource system initialized");

        ScriptLoader.initialize();

        getVMOptions();

        ExtensionManager.loadStep(Extension.NONE);

        ExtensionManager.loadStep(Extension.LWJGL);

        SystemInfo.querySystemInfo();
        OpenGG.loadConfigs();
        GGConsole.log("Loaded configuration files");

        ExtensionManager.loadStep(Extension.CONFIG);

        if(client)
            initializeClient(options.getWindowOptions());
        else
            initializeServer();


        WorldEngine.initialize();
        GGConsole.log("World Engine initialized");

        PhysicsEngine.initialize();
        GGConsole.log("Physics Engine initialized");

        SystemInfo.queryEngineInfo();

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
            if(render) runRender();
        }

        GGConsole.log("OpenGG closing...");
        GGInfo.setEnded(true);
        if(!force){
            closeEngine();
        }
        writeLog();
    }

    private static void runUpdate() {
        float realDelta = time.getDeltaSec();
        if(realDelta < targetUpdate) {
            try {
                Thread.sleep((long) ((targetUpdate-realDelta)*1000));
                realDelta = targetUpdate;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if(realDelta > targetUpdate && targetUpdate != 0 && OpenGG.getDebugOptions().warnOnMissedTarget()){
            GGConsole.warning("Last update cycle missed the target update! (" + realDelta + " sec instead of " + targetUpdate + " sec)");
        }

        var gameDelta = overrideUpdate > 0f ? overrideUpdate : realDelta;
        Allocator.update();
        PerformanceManager.update(realDelta);
        Executor.getExecutor().update(realDelta);
        ExtensionManager.update(realDelta);
        WorldEngine.update(gameDelta);
        AnimationManager.update(gameDelta);
        PhysicsEngine.updatePhysics(gameDelta);
        getApp().update(gameDelta);
        SoundtrackHandler.update();
        GUIController.update(realDelta);
        NetworkEngine.update(realDelta);
    }

    private static void runInput() {
        MouseController.update();
    }

    private static void runRender() {
        RenderEngine.startFrame();
        WindowController.update();
        getApp().render();
        ExtensionManager.render();
        RenderEngine.render();
        RenderEngine.endFrame();
        NativeResourceManager.runQueuedFinalization();
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

    public static InitializationOptions getInitOptions() {
        return initOptions;
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

    private static List<File> recursiveLoadConfigs(File directory) {
        var allfiles = directory.listFiles();
        if(allfiles == null){
            throw new RuntimeException("Failed to find config directory " + directory.getAbsolutePath());
        }
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


    private static void closeEngine(){
        RenderEngine.destroy();
        SoundEngine.destroy();
        NetworkEngine.close();
        GGConsole.log("Audio controller has been finalized");
        WindowController.destroy();
        ThreadManager.destroy();
        GGConsole.log("Thread Manager has closed all remaining threads");
        GGConsole.log("OpenGG has closed gracefully, application can now be ended");
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

    private static void writeLog(){
        if(test) {
        }
        //GGConsole.writeLog(startTime);
    }

    private static void writeUserData(){
        String error = SystemInfo.getInfo();
        GGConsole.log(error);
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
        return mainThread == Thread.currentThread();
    }

    /**
     * Gives the engine the given {@link Runnable} to run in the next cycle.<br>
     * This functionality is useful to be able to run functions that require the main thread (For example requiring OpenGL calls)
     * @param e Overrided Runnable to be run
     */
    public static Executor.Sleeper asyncExec(Runnable e){
        return Executor.async(e);
    }

    public static void onMainThread(Runnable e){
        if(inMainThread())
            e.run();
        else
            asyncExec(e);
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

    /**
     * Sets the target update time, in seconds, for the engine <br>
     *     If this is set to a nonzero value, the engine will delay each tick until the
     *     target update is met (For example, if targetUpdate is 0.1 sec but the last tick took 0.03 sec, it will
     *     add a real time delay of 0.07 sec) <br>
     *     Note, this does not guarantee that a tick will take the given time, as it cannot do anything about a tick
     *     that takes longer than the target.
     *
     * @param time Target update time in seconds
     */
    public static void setTargetUpdateTime(float time) {
        OpenGG.targetUpdate = time;
    }

    /**
     * Sets the override delta update time for the engine, or disable override if its 0 <br>
     *     This override is used in place of the real time delta, meaning that for every tick, all subsystems are fed
     *     this value regardless of how much time actually passed.
     *     <br>
     *     Note, this delta time will be applied to all elements that use the engine's internal delta time, including
     *     internal performance counters and the executor system. Additionally, as this does not actually slow
     *     down or speed up the real time simulation, it can conflict with engine or application components that require
     *     real time in some way, most importantly the networking system. Therefore, great care should be taken when
     *     enabling this feature on networked application.
     * @param time
     */
    public static void setOverrideUpdateTIme(float time) {
        OpenGG.overrideUpdate = time;
    }

    public static void enableRendering(boolean render) {
        OpenGG.render = render;
    }

    /**
     * Returns the current set of debug options and flags enabled in the engine <br>
     *     Most can be updated as needed as the engine runs with no issue2
     * @return
     */
    public static DebugOptions getDebugOptions() {
        return options;
    }

    /**
     * Contains options for various debugging and detailed logging options for the OpenGG Engine
     */
    public static class DebugOptions{
        private boolean verbose = false;
        private boolean warnOnMissedTarget = false;
        private boolean logOnComponentCreation = false;

        /**
         * Enables all debug logging options
         * @return
         */
        public boolean isVerbose() {
            return verbose;
        }

        public DebugOptions setVerbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        /**
         * Returns if the engine should log if a tick length exceeded the target
         * @return
         */
        public boolean warnOnMissedTarget() {
            return warnOnMissedTarget || verbose;
        }

        public DebugOptions setWarnOnMissedTarget(boolean warnOnMissedTarget) {
            this.warnOnMissedTarget = warnOnMissedTarget;
            return this;
        }

        /**
         * Returns if all component creations should be logged
         * @return
         */
        public boolean logOnComponentCreation() {
            return logOnComponentCreation || verbose;
        }

        public DebugOptions setLogOnComponentCreation(boolean logOnComponentCreation) {
            this.logOnComponentCreation = logOnComponentCreation;
            return this;
        }
    }
}
