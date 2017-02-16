/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.exceptions.IncompatibleWindowFormatException;
import com.opengg.core.render.window.GLFWWindow;
import static com.opengg.core.render.window.RenderUtil.endFrame;
import static com.opengg.core.render.window.RenderUtil.startFrame;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowInfo;
import static com.opengg.core.render.window.WindowOptions.GLFW;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class OpenGG implements ConsoleListener{
    public static Window window;
    public static GGApplication app;
    public static World curworld;
    public static boolean lwjglinit = false;
    static Date startTime;
    static boolean end = false;
    static boolean force = false;
    static boolean verbose = false;
    static boolean test = false;
      
    public static void initialize(GGApplication app, WindowInfo windowinfo){       
        
        startTime = Calendar.getInstance().getTime();
        
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
        
        lwjglinit = true;
        
        String verb = System.getProperty("gg.verbose");
        String stest = System.getProperty("gg.istest");
        if(verb != null)
            if(verb.equals("true"))
                verbose = true;
        
        if(stest != null)
            if(stest.equals("true"))
                test = true;
        
        GGConsole.log("OpenGG initializing, running on " + System.getProperty("os.name") + ", " + System.getProperty("os.arch"));
        
        OpenGG.app = app;
        if(windowinfo.type == GLFW)
            window = new GLFWWindow(windowinfo);
        else
            throw new IncompatibleWindowFormatException("Window type passed in is unknown!");
        
        GGConsole.log("Window generation successful, using OpenGL context version " + RenderEngine.getGLVersion());
        
        initializeRenderEngine();  
        RenderEngine.checkForGLErrors();
        
        initializeAudioController();      
        
        curworld = new World();
        GGConsole.log("OpenGG initialization complete, running application setup");
        try{
            app.setup();
            RenderEngine.checkForGLErrors();
            GGConsole.log("Application setup complete");
            run();
        }catch (Exception e){
            GGConsole.error("Uncaught exception: " + e.toString());
            e.printStackTrace();
            try {Thread.sleep(10);} catch (InterruptedException ex) {}
            closeEngine();
            writeLog();
            System.exit(0);
        }
    }
    
    public static void initializeHeadless(GGApplication app){
        startTime = Calendar.getInstance().getTime();
        
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
        
        lwjglinit = true;
        
        String verb = System.getProperty("gg.verbose");
        String stest = System.getProperty("gg.istest");
        if(verb != null)
            if(verb.equals("true"))
                verbose = true;
        
        if(stest != null)
            if(stest.equals("true"))
                test = true;
        
        GGConsole.log("OpenGG initializing, running on " + System.getProperty("os.name") + ", " + System.getProperty("os.arch"));
        
        OpenGG.app = app;
        
        curworld = new World();
        
        try{
            app.setup();
            GGConsole.log("Application setup complete");
            runHeadless();
        }catch (Exception e){
            GGConsole.error("Uncaught exception: " + e.getMessage());
            e.printStackTrace();
            try {Thread.sleep(10);} catch (InterruptedException ex) {}
            closeEngine();
            writeLog();
            System.exit(0);
        }
    }
    
    public static void runHeadless(){
        while(!end){
            app.update();
            WorldEngine.update();
            //GGConsole.pollInput();
        }
        if(!force){
            closeEngine();
        }
        writeLog();
    }
    
    public static void run(){
        
        while (!window.shouldClose() && !end) {
            startFrame();
            try{
                app.render();
                app.update();
            }catch (Exception e){
                GGConsole.error("Uncaught exception during application runtime: " + e.toString());
                e.printStackTrace();
                endFrame();
                break;
            }
            WorldEngine.update();
            RenderEngine.checkForGLErrors();
            endFrame();
            //GGConsole.pollInput();
        }
        if(!force){
            GGConsole.destroy();
            GGConsole.log("OpenGG has closed gracefully, application can now be ended");
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
    }
    
    public static void loadState(){
        try (DataInputStream dis = new DataInputStream(new FileInputStream(Resource.getLocal("dump.sav")))){
            int worldsize = dis.readInt();
            byte[] worlddata = new byte[worldsize];
            for(int i = 0; i < worlddata.length; i++){
                worlddata[i] = dis.readByte();
            }
            World w = Deserializer.deserialize(ByteBuffer.wrap(worlddata));
            //curworld = w;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OpenGG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OpenGG.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void endApplication(){
        end = true;
    }
    
    public static void forceEnd(){
        force = true;
        end = true;
    }
    
    private static void closeEngine(){
        RenderEngine.destroy();      
        AudioController.destroy();
        GGConsole.destroy();
        GGConsole.log("OpenGG has closed gracefully, application can now be ended");
    }
    
    private static void writeLog(){
        if(test) return;
        GGConsole.writeLog(startTime);
    }

    @Override
    public void onConsoleInput(String s) {
        if(s.equalsIgnoreCase("quit")){
            endApplication();
        }
        if(s.equalsIgnoreCase("forcequit")){
            forceEnd();
        }
    }
}
