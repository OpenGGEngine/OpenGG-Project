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
import com.opengg.core.world.World;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
    static boolean end = false;
    static boolean force = false;
    static boolean verbose = false;
    
    public static void initialize(GGApplication app, WindowInfo windowinfo){        
        if(System.getProperty("os.name").contains("Windows")){
            System.setProperty("org.lwjgl.librarypath", new File("natives\\windows").getAbsolutePath());
        }else if(System.getProperty("os.name").contains("OS X")){
           System.setProperty("org.lwjgl.librarypath", new File("natives\\osx").getAbsolutePath());
        }else if(System.getProperty("os.name").contains("Linux")){
            System.setProperty("org.lwjgl.librarypath", new File("natives\\linux").getAbsolutePath());
        }
        
        GGConsole.log("OpenGG initialized, running on " + System.getProperty("os.name"));
        String verb = System.getProperty("gg.verbose");
        if(verb != null)
            if(verb.equals("true"))
                verbose = true;
        
        OpenGG.app = app;
        if(windowinfo.type == GLFW)
            window = new GLFWWindow(windowinfo);
        else
            throw new IncompatibleWindowFormatException("Window type passed in is unknown!");
        
        GGConsole.log("Window generation successful");
        
        if(System.getProperty("gg.istest") != null){
            initializeRenderEngine(app);
            GGConsole.log("Render engine initialized");
            GGConsole.warning("Using test mode, external shaders will not load!");
        }else{
            initializeRenderEngine();
            GGConsole.log("Render engine initialized");
        }
        
        initializeAudioController();
        GGConsole.log("Audio controller initialized");
        curworld = WorldManager.getDefaultWorld();
        
        app.setup();
    }
    
    public static void initializeNoWindow(GGApplication app){
        OpenGG.app = app;
        curworld = WorldManager.getDefaultWorld();
        app.setup();
    }
    
    public static void runNoWindow(){
        while(!end){
            app.update();
            UpdateEngine.update();
            GGConsole.pollInput();
        }
    }
    
    public static void run(){
        while (!window.shouldClose() && !end) {
            startFrame();
            app.render();
            app.update();
            UpdateEngine.update();
            endFrame();
            //GGConsole.pollInput();
        }
        if(!force){
            closeEngine();
        }
    }
    
    private static void initializeRenderEngine(){
        if(window.getSuccessfulConstruction() && RenderEngine.initialized == false){
            try {
                URL verts = new File("resources\\glsl\\shader.vert").toURI().toURL();
                URL frags = new File("resources\\glsl\\shader.frag").toURI().toURL();
                URL geoms = new File("resources\\glsl\\shader.geom").toURI().toURL();
                RenderEngine.init(verts, frags, geoms);
            } catch (MalformedURLException ex) {
                Logger.getLogger(OpenGG.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Failed to load shaders!");
            }
        }
    }
    
    private static void initializeRenderEngine(Object obj){
        if(window.getSuccessfulConstruction() && RenderEngine.initialized == false){
            URL verts = obj.getClass().getResource("glsl/shader.vert");
            URL frags = obj.getClass().getResource("glsl/shader.frag");
            URL geoms = obj.getClass().getResource("glsl/shader.geom");
            RenderEngine.init(verts, frags, geoms);
        }
    }
    
    private static void initializeAudioController(){
       if(window.getSuccessfulConstruction() && AudioController.initialized == false){
            AudioController.init();
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
        GGConsole.log("Render engine has finalized");
        AudioController.destroy();
        GGConsole.log("Audio controller has finalized");
        GGConsole.destroy();
        
        GGConsole.log("OpenGG has closed gracefully, application can now be ended");
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
