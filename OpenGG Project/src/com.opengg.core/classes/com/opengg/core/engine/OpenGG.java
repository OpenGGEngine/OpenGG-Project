
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.console.GGConsole;
import com.opengg.core.extension.Extension;
import com.opengg.core.extension.ExtensionManager;
import static com.opengg.core.render.window.RenderUtil.endFrame;
import static com.opengg.core.render.window.RenderUtil.startFrame;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.system.SystemInfo;
import com.opengg.core.thread.ThreadManager;
import com.opengg.core.util.Time;
import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Primary controller of all functionality in the OpenGG Engine
 * @author Javier
 */
public class OpenGG{
    public static final String version = "0.1";
    
    private static GGApplication app;
    private static boolean lwjglinit = false;
    private static List<ExecutableContainer> executables = Collections.synchronizedList(new LinkedList<>());
    private static Date startTime;
    private static boolean head = false;
    private static boolean end = false;
    private static boolean force = false;
    private static boolean verbose = false;
    private static boolean test = false;
    private static Time time;
    private static Thread mainthread;
      
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
                GGConsole.log("Null WindowInfo, assuming headless");
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
    
    private static void initializeLocalClient(WindowInfo windowinfo){       
        WindowController.setup(windowinfo);
        GGConsole.log("Window generation successful, using OpenGL context version " + RenderEngine.getGLVersion());
        
        SystemInfo.queryOpenGLInfo();
        GGConsole.log("System info queried");
        
        initializeRenderEngine();  
        RenderEngine.checkForGLErrors();
        
        initializeAudioController();      
        BindController.initialize();
        
        ExtensionManager.loadStep(Extension.GRAPHICS);
    }
    
    private static void initializeLocal(GGApplication ggapp, WindowInfo info, boolean client){
        time = new Time();
        startTime = Calendar.getInstance().getTime();
        mainthread = Thread.currentThread();
        head = client;
        app = ggapp;
        
        
        ThreadManager.initialize();
        ThreadManager.runRunnable(new GGConsole(), "consolethread");
        GGConsole.addListener(new OpenGGCommandExtender());
        GGConsole.log("OpenGG initializing, running on " + System.getProperty("os.name") + ", " + System.getProperty("os.arch"));
        
        Resource.initialize();
        
        getVMOptions();
        
        ExtensionManager.loadStep(Extension.NONE);

        linkLWJGL();
        
        ExtensionManager.loadStep(Extension.LWJGL);
        
        SystemInfo.querySystemInfo();
        Config.reloadConfigs();

        ExtensionManager.loadStep(Extension.CONFIG);
        
        if(client)
            initializeLocalClient(info);

        WorldEngine.initialize();
        PhysicsEngine.initialize();
        
        GGConsole.log("Application setup beginning");
        app.setup();
        WorldEngine.useWorld(WorldEngine.getCurrent());
        GGConsole.log("Application setup complete");
        GGConsole.log("OpenGG initialized in " + time.getDeltaMs() + " milliseconds");
        
        if(client)
            run();
        else
            runHeadless();
    }
    
    private static void runHeadless(){
        while(!end){
            float delta = time.getDeltaSec();
            app.update(delta);
            WorldEngine.update(delta);
            processExecutables(delta);
            
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Logger.getLogger(OpenGG.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(!force){
            GGConsole.log("OpenGG has closed gracefully, application can now be ended");
        }
        
        writeLog();
    }
    
    private static void run(){
        while (!getWindow().shouldClose() && !end) {
            float delta = time.getDeltaSec();
            processExecutables(delta);
            
            app.update(delta);
            ExtensionManager.update(delta);
            WorldEngine.update(delta);
            PhysicsEngine.updatePhysics(delta);
            SoundtrackHandler.update();
            
            WindowController.update();
            startFrame();
            app.render();
            ExtensionManager.render();
            RenderEngine.draw();
            RenderEngine.checkForGLErrors();
            endFrame();                   
        }
        
        GGConsole.log("OpenGG closing...");
        end = true;
        if(!force){
            closeEngine();
        }
        writeLog();
    }
    
    private static void initializeRenderEngine(){
        if(getWindow().getSuccessfulConstruction() && RenderEngine.isInitialized() == false){
            RenderEngine.init();
            GGConsole.log("Render engine initialized");
        }
    }
    
    private static void initializeAudioController(){
       if(getWindow().getSuccessfulConstruction() && AudioController.isInitialized() == false){
            AudioController.init();
            GGConsole.log("Audio controller initialized");
        }   
    }
    
    public static void linkLWJGL(){
        GGConsole.log("Loading native libraries...");
        if(System.getProperty("os.name").contains("Windows")){
            System.setProperty("org.lwjgl.librarypath", new File("native" + File.separator + "windows").getAbsolutePath());
        }else if(System.getProperty("os.name").contains("OS X")){
           System.setProperty("org.lwjgl.librarypath", new File("native" + File.separator + "osx").getAbsolutePath());
        }else if(System.getProperty("os.name").contains("Linux")){
            System.setProperty("org.lwjgl.librarypath", new File("native" + File.separator + "linux").getAbsolutePath());
        }else{
            GGConsole.error("OpenGG is not supported on " + System.getProperty("os.name") + ", exiting...");
            writeLog();
            System.exit(0);
        }
         lwjglinit = true;
        GGConsole.log("LWJGL has been loaded");
    }
    
    private static void getVMOptions(){ 
        String verb = System.getProperty("gg.verbose");
        String stest = System.getProperty("gg.istest");
        if(verb != null)
            if(verb.equals("true"))
                verbose = true;
        
        if(stest != null)
            if(stest.equals("true"))
                test = true;
    }
    
    /**
     * Marks the current instance of OpenGG to end safely on the next update cycle, will run all cleanup code
     */
    public static void endApplication(){
        GGConsole.log("Application end has been requested");
        end = true;
    }
    
    /**
     * Force ends the application immediately.<br>
     * Because it forces the application, including all other threads, to end (basically calling {@code System.exit(0)}), 
     * it does not cleanup resources currently in use.
     * Only use in extreme circumstances (like in case of a program freeze)
     */
    public static void forceEnd(){
        GGConsole.warning("Application is force quitting");
        System.exit(0);
        force = true;
        end = true;
    }
    
    /**
     * Returns the current {@link com.opengg.core.render.window.Window} for this instance
     * @return The current window
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
        return end;
    }
    
    /**
     * Returns if the LWJGL library's natives have been loaded and initialized
     * @return If LWJGL has been initialized
     */
    public static boolean lwjglInitialized(){
        return lwjglinit;
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
        GGConsole.log("Render engine has finalized");
        AudioController.destroy();
        GGConsole.log("Audio controller has been finalized");
        WindowController.destroy();
        ThreadManager.destroy();
        GGConsole.log("Thread Manager has closed all remaining threads");
        GGConsole.log("OpenGG has closed gracefully, application can now be ended");
    }
    
    private static void writeLog(){
        if(test) return;
        //GGConsole.writeLog(startTime);
    }
    
    private static void writeErrorLog(){
        if(test) return;
        String error = SystemInfo.getInfo();
        GGConsole.writeLog(startTime, error, "error");
    }

    /**
     * Returns the starting time for this OpenGG instance, starting from the initial call to OpenGG.initialize()
     * @return Start time
     */
    public static Date getStartTime() {
        return startTime;
    }
    
    /**
     * Returns if the thread in which this method is called is the main thread
     * @return If is in main thread
     */
    public static boolean inMainThread(){
        return mainthread == Thread.currentThread();
    }
    
    private static void exec(ExecutableContainer e){
        executables.add(e);
    }
    
    /**
     * Gives the engine the given {@link Executable} to run in the next cycle.<br>
     * This functionality is useful to be able to run functions that require the main thread (For example requiring OpenGL calls)
     * @param e Overrided executable to be run
     */
    public static void asyncExec(Executable e){
        exec(new ExecutableContainer(e));
    }
    
    /**
     * Gives the engine the given {@link Executable} to run in the amount of seconds given with one cycle length deviation 
     * @param seconds In how many seconds to run the executable
     * @param e Overrided executable to be run
     */
    public static void asyncExec(float seconds, Executable e){
        exec(new ExecutableContainer(e, seconds));
    }
    
    /**
     * Gives the engine the given {@link Executable} to run in the next cycle.<br>
     * This functionality is useful to be able to run functions that require the main thread (For example requiring OpenGL calls).<br>
     * This version blocks the current thread until the executable is run
     * @param e Overrided executable to be run
     */
    public static void syncExec(Executable e){
        ExecutableContainer execcont = new ExecutableContainer(e);
        
        if(!inMainThread()){
            GGConsole.error("syncExec cannot be called in OpenGG thread!");
            throw new RuntimeException("syncExec cannot be called in OpenGG thread!");
        }
        
        exec(execcont);

        while(!(execcont.executed)){
            try{
        	Thread.sleep(2);
            }catch(InterruptedException ex){}
        }
    }
    
    private static boolean hasExecutables(){
        for(ExecutableContainer ex : executables){
            if(ex.elapsed > ex.timetoexec){
                return true;
            }
        }
        return false;
    }
    
    private static void processExecutables(float delta){   
        for(ExecutableContainer ex : executables){
            ex.elapsed += delta;
        }
        while(hasExecutables()){
            List<ExecutableContainer> tempex = new LinkedList<>();
            for(ExecutableContainer ex : executables){
                if(ex.elapsed > ex.timetoexec){
                    tempex.add(ex);
                }
            }
            
            for(ExecutableContainer ex : tempex){
                executables.remove(ex);
            }
            
            
            for(ExecutableContainer e : tempex){
                e.exec.execute();
                e.executed = true;
            }
        }
        
    }
}
