/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.exceptions.IncompatibleWindowFormatException;
import com.opengg.core.render.window.GLFWWindow;
import static com.opengg.core.render.window.RenderUtil.endFrame;
import static com.opengg.core.render.window.RenderUtil.startFrame;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowInfo;
import static com.opengg.core.render.window.WindowOptions.GLFW;
import com.opengg.core.thread.ThreadManager;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;
import com.opengg.core.world.World;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
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
    public static final String version = "0.0.1a1";
    
    public static Window window;
    public static GGApplication app;
    public static World curworld;
    public static boolean lwjglinit = false;
    static List<Executable> executables = new LinkedList<>();
    static Date startTime;
    static boolean head = false;
    static boolean end = false;
    static boolean force = false;
    static boolean verbose = false;
    static boolean test = false;
      
    private OpenGG(){}
    
    /**
     * Initializes an instance of the OpenGG Engine. This gives full runtime control of the program to OpenGG, so no code will run past this call until the engine closes
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
        if(windowinfo.type == GLFW)
            window = new GLFWWindow(windowinfo);
        else
            throw new IncompatibleWindowFormatException("Window type passed in is unknown!");
        
        SystemInfo.queryOpenGLInfo();
        GGConsole.log("Window generation successful, using OpenGL context version " + RenderEngine.getGLVersion());
        
        initializeRenderEngine();  
        RenderEngine.checkForGLErrors();
        
        initializeAudioController();      
        BindController.initialize();
    }
    
    private static void initializeLocal(GGApplication app, WindowInfo info, boolean client){
        startTime = Calendar.getInstance().getTime();
        head = client;
        
        linkLWJGL();
        lwjglinit = true;
        
        ThreadManager.initialize();
        SystemInfo.querySystemInfo();
        Config.reloadConfigs();
        
        String verb = System.getProperty("gg.verbose");
        String stest = System.getProperty("gg.istest");
        if(verb != null)
            if(verb.equals("true"))
                verbose = true;
        
        if(stest != null)
            if(stest.equals("true"))
                test = true;
        
        ThreadManager.runRunnable(new GGConsole(), "consolethread");
        GGConsole.addListener(new OpenGGCommandExtender());
        GGConsole.log("OpenGG initializing, running on " + System.getProperty("os.name") + ", " + System.getProperty("os.arch"));
        
        OpenGG.app = app;
        
        if(client)
            initializeGraphics(info);
        
        curworld = new World();

        GGConsole.log("Application setup beginning");
        app.setup();
        GGConsole.log("Application setup complete");
        if(client)
            run();
        else
            runHeadless();
    }
    
    public static void runHeadless(){
        while(!end){
            app.update();
            WorldEngine.update();
            for(Executable e : executables){
                    e.execute();
            }
            executables.clear();
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
        while (!window.shouldClose() && !end) {
            startFrame();
            app.render();
            RenderEngine.draw();
            endFrame();
            RenderEngine.checkForGLErrors();
            
            processExecutables();
            app.update();
            WorldEngine.update();
            SoundtrackHandler.update();
        }
        end = true;
        if(!force){
            closeEngine();
        }
        writeLog();
    }
    
    private static void initializeRenderEngine(){
        if(window.getSuccessfulConstruction() && RenderEngine.initialized == false){
            RenderEngine.init();
            GGConsole.log("Render engine initialized");
        }
    }
    
    private static void initializeAudioController(){
       if(window.getSuccessfulConstruction() && AudioController.initialized == false){
            AudioController.init();
            GGConsole.log("Audio controller initialized");
        }   
    }
    
    public static void saveState(){
        GGConsole.log("Saving state...");
        try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(Resource.getLocal("dump.sav")))) {
            byte[] world = Serializer.serialize(curworld);
            dos.writeInt(world.length);
            dos.write(world);
            dos.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OpenGG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OpenGG.class.getName()).log(Level.SEVERE, null, ex);
        }
        GGConsole.log("State has been saved");
    }
    
    public static void loadState(){
        try (DataInputStream dis = new DataInputStream(new FileInputStream(Resource.getLocal("dump.sav")))){
            int worldsize = dis.readInt();
            byte[] worlddata = new byte[worldsize];
            for(int i = 0; i < worlddata.length; i++){
                worlddata[i] = dis.readByte();
            }
            World w = Deserializer.deserialize(ByteBuffer.wrap(worlddata));
            curworld = w;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OpenGG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OpenGG.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void linkLWJGL(){
        GGConsole.log("Loading native libraries...");
        if(System.getProperty("os.name").contains("Windows")){
            System.setProperty("org.lwjgl.librarypath", new File("natives\\windows").getAbsolutePath());
        }else if(System.getProperty("os.name").contains("OS X")){
           System.setProperty("org.lwjgl.librarypath", new File("natives\\osx").getAbsolutePath());
        }else if(System.getProperty("os.name").contains("Linux")){
            System.setProperty("org.lwjgl.librarypath", new File("natives\\linux").getAbsolutePath());
        }else{
            GGConsole.error("OpenGG is not supported on " + System.getProperty("os.name") + ", exiting...");
            writeLog();
            System.exit(0);
        }
        GGConsole.log("LWJGL has been loaded");
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
    
    public static boolean getEnded(){
        return end;
    }
    
    private static void closeEngine(){
        RenderEngine.destroy();      
        GGConsole.log("Render engine has finalized");
        AudioController.destroy();
        GGConsole.log("Audio controller has been finalized");
        window.destroy();
        ThreadManager.destroy();
        GGConsole.log("Thread Manager has closed all remaining threads");
        GGConsole.log("OpenGG has closed gracefully, application can now be ended");
    }
    
    private static void writeLog(){
        if(test) return;
        GGConsole.writeLog(startTime);
    }
    
    private static void writeErrorLog(){
        if(test) return;
        String error = SystemInfo.getInfo();
        GGConsole.writeLog(startTime, error, "error");
    }

    public static void addExecutable(Executable e){
        executables.add(e);
    }
    
    public static boolean hasExecutables(){
        return !executables.isEmpty();
    }
    
    public static void processExecutables(){
        for(Executable e : executables){
            e.execute();
        }
        executables.clear();
    }
}
