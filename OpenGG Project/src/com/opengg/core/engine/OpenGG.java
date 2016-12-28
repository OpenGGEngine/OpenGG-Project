/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.exceptions.IncompatibleWindowFormatException;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.window.GLFWWindow;
import static com.opengg.core.render.window.RenderUtil.endFrame;
import static com.opengg.core.render.window.RenderUtil.startFrame;
import com.opengg.core.render.window.Window;
import com.opengg.core.render.window.WindowInfo;
import static com.opengg.core.render.window.WindowOptions.GLFW;
import com.opengg.core.world.World;
import java.net.URL;

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
    
    public static void initialize(GGApplication app, WindowInfo windowinfo){        
        OpenGG.app = app;
        if(windowinfo.type == GLFW){
            window = new GLFWWindow(windowinfo);
        }else{
            throw new IncompatibleWindowFormatException("Window type passed in is unknown!");
        }
        initializeRenderEngine(app);
        initializeAudioController();
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
            URL verts = ShaderController.class.getResource("glsl/shader.vert");
            URL frags = ShaderController.class.getResource("glsl/shader.frag");
            URL geoms = ShaderController.class.getResource("glsl/shader.geom");
            RenderEngine.init(verts, frags, geoms);
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
        AudioController.destroy();
        GGConsole.destroy();
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
