
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.extension.Extension;
import com.opengg.core.extension.ExtensionManager;
import static com.opengg.core.render.window.RenderUtil.endFrame;
import static com.opengg.core.render.window.RenderUtil.startFrame;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.thread.ThreadManager;
import com.opengg.core.util.Time;
import com.opengg.core.world.World;
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
    
    static GGApplication app;
    static boolean lwjglinit = false;
    static List<ExecutableContainer> executables = Collections.synchronizedList(new LinkedList<>());
    static Date startTime;
    static boolean head = false;
    static boolean end = false;
    static boolean force = false;
    static boolean verbose = false;
    static boolean test = false;
    static Time time;
    static Thread mainthread;
      
    private OpenGG(){}
    
    /**
     * Initializes the OpenGG Engine. This gives full runtime control of the program to OpenGG, so no code will run past this call until the engine closes
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
    
    private static void initializeGraphics(WindowInfo windowinfo){       
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
        
        Resource.initialize();
        getVMOptions();
        
        ExtensionManager.loadStep(Extension.NONE);

        linkLWJGL();
        
        ExtensionManager.loadStep(Extension.LWJGL);
        
        ThreadManager.initialize();
        SystemInfo.querySystemInfo();
        Config.reloadConfigs();
        
        ThreadManager.runRunnable(new GGConsole(), "consolethread");
        GGConsole.addListener(new OpenGGCommandExtender());
        GGConsole.log("OpenGG initializing, running on " + System.getProperty("os.name") + ", " + System.getProperty("os.arch"));
        WorldEngine.initialize();
        
        ExtensionManager.loadStep(Extension.CONFIG);
        
        if(client)
            initializeGraphics(info);

        GGConsole.log("Application setup beginning");
        app.setup();
        WorldEngine.rescanCurrent();
        GGConsole.log("Application setup complete");
        GGConsole.log("OpenGG initialized in " + time.getDeltaMs() + " milliseconds");
        
        if(client)
            run();
        else
            runHeadless();
    }
    
    public static void runHeadless(){
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
    
    public static void run(){
        while (!getWindow().shouldClose() && !end) {
            WindowController.update();
            startFrame();
            app.render();
            ExtensionManager.render();
            RenderEngine.draw();
            RenderEngine.checkForGLErrors();
            endFrame();
            
            float delta = time.getDeltaSec();
            processExecutables(delta);
            app.update(delta);
            ExtensionManager.update();
            WorldEngine.update(delta);
            PhysicsEngine.updatePhysics(delta);
            SoundtrackHandler.update();
        }
        GGConsole.log("OpenGG closing...");
        end = true;
        if(!force){
            closeEngine();
        }
        writeLog();
    }
    
    private static void initializeRenderEngine(){
        if(getWindow().getSuccessfulConstruction() && RenderEngine.initialized == false){
            RenderEngine.init();
            GGConsole.log("Render engine initialized");
        }
    }
    
    private static void initializeAudioController(){
       if(getWindow().getSuccessfulConstruction() && AudioController.initialized == false){
            AudioController.init();
            GGConsole.log("Audio controller initialized");
        }   
    }
    
    public static void linkLWJGL(){
        GGConsole.log("Loading native libraries...");
        if(System.getProperty("os.name").contains("Windows")){
            System.setProperty("org.lwjgl.librarypath", new File("natives" + File.separator + "windows").getAbsolutePath());
        }else if(System.getProperty("os.name").contains("OS X")){
           System.setProperty("org.lwjgl.librarypath", new File("natives" + File.separator + "osx").getAbsolutePath());
        }else if(System.getProperty("os.name").contains("Linux")){
            System.setProperty("org.lwjgl.librarypath", new File("natives" + File.separator + "linux").getAbsolutePath());
        }else{
            GGConsole.error("OpenGG is not supported on " + System.getProperty("os.name") + ", exiting...");
            writeLog();
            System.exit(0);
        }
         lwjglinit = true;
        GGConsole.log("LWJGL has been loaded");
    }
    
    public static void getVMOptions(){
          
        String verb = System.getProperty("gg.verbose");
        String stest = System.getProperty("gg.istest");
        if(verb != null)
            if(verb.equals("true"))
                verbose = true;
        
        if(stest != null)
            if(stest.equals("true"))
                test = true;
    }
    
    public static void endApplication(){
        GGConsole.log("Application end has been requested");
        end = true;
    }
    
    public static void forceEnd(){
        GGConsole.warning("Application has been asked to force quit");
        force = true;
        end = true;
    }
    
    public static Window getWindow() {
        return WindowController.getWindow();
    }

    public static GGApplication getApp() {
        return app;
    }
    
    public static boolean getEnded(){
        return end;
    }
    
    public static boolean lwjglInitialized(){
        return lwjglinit;
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

    public static boolean inMainThread(){
        return mainthread == Thread.currentThread();
    }
    
    private static void exec(ExecutableContainer e){
        executables.add(e);
    }
    
    public static void asyncExec(Executable e){
        exec(new ExecutableContainer(e));
    }
    
    public static void syncExec(Executable e){
        ExecutableContainer execcont = new ExecutableContainer(e);
        
        if(!inMainThread()){
            GGConsole.error("syncExec cannot be called in OpenGG thread!");
            throw new RuntimeException("syncExec cannot be called in OpenGG thread!");
        }
        
        exec(execcont);

        while(!(execcont.executed)){
            try{
        	Thread.sleep(5);
            }catch(InterruptedException ex){}
        }
    }
    
    public static boolean hasExecutables(){
        return !executables.isEmpty();
    }
    
    public static void processExecutables(float delta){
        while(!executables.isEmpty()){
            List<ExecutableContainer> tempex = new LinkedList<>();
            for(ExecutableContainer ex : executables){
                ex.elapsed += delta;
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
