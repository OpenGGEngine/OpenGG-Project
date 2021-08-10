/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.system;

import com.opengg.core.GGInfo;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;

/**
 *
 * @author Javier
 */
public class SystemInfo {
    static Map<String, String> glinfo = new LinkedHashMap<>();
    static Map<String, String> javainfo = new LinkedHashMap<>();
    static Map<String, String> sysinfo = new LinkedHashMap<>();
    static Map<String, String> engineinfo = new LinkedHashMap<>();
    
    private static final boolean dxdiag = false;
    
    public static void querySystemInfo(){
        javainfo.put("Java Version", "" +System.getProperty("java.version"));
        javainfo.put("JVM Name", "" +System.getProperty("java.vm.name"));
        javainfo.put("JVM Vendor", "" +System.getProperty("java.vm.vendor"));
        javainfo.put("Maximum Runtime Memory", ""+Runtime.getRuntime().maxMemory());
        javainfo.put("Used Runtime Memory",  ""+Runtime.getRuntime().totalMemory());
        javainfo.put("Free Runtime Memory",  ""+Runtime.getRuntime().freeMemory());
        javainfo.put("JVM CPU Availability",  ""+Runtime.getRuntime().availableProcessors());

        sysinfo.put("Operating System", ""+System.getProperty("os.name"));
        sysinfo.put("OS Architecture", ""+System.getProperty("os.arch"));
        sysinfo.put("Processor", ""+System.getenv("PROCESSOR_IDENTIFIER"));
        sysinfo.put("CPU Count", ""+System.getenv("NUMBER_OF_PROCESSORS"));
        /*
        genDxDiag();
        if(dxdiag){
            try (BufferedReader br = new BufferedReader(new FileReader("dxdiag.txt"));){
                String line;
                while((line = br.readLine()) != null){
                    if(line.trim().startsWith("Operating System:"))
                        sysinfo.put("Operating System", line.trim());
                    if(line.trim().startsWith("Processor:"))
                        sysinfo.put("Processor", line.trim());
                    if(line.trim().startsWith("Card name:"))
                        sysinfo.put("Card name", line.trim());
                    if(line.trim().startsWith("Current Mode:"))
                        glinfo.put("Current Mode", line.trim());
                    if(line.trim().startsWith("Native Mode:"))
                        glinfo.put("Native Mode", line.trim());
                    if(line.trim().startsWith("Display Memory:"))
                        sysinfo.put("Display Memory", line.trim());
                    if(line.trim().startsWith("Dedicated Memory:"))
                        sysinfo.put("Dedicated Memory", line.trim());
                    if(line.trim().startsWith("Shared Memory:"))
                        sysinfo.put("Shared Memory", line.trim());
                    if(line.trim().startsWith("Memory:"))
                        sysinfo.put("Memory", line.trim());
                    if(line.trim().startsWith("System Model:"))
                        sysinfo.put("System Model", line.trim());
                }
            } catch (IOException ex) {
                GGConsole.warning("Failed to open generated DxDiag file, Java information may be inaccurate");
            }
        }
         */

    }

    public static void queryRendererInfo(String renderer){
        glinfo.put("Renderer", renderer);
        if(renderer.equals("OpenGL")){
            String glVersion = GGInfo.getGlVersion();
            glinfo.put("Requested GL Version", glVersion);
            glinfo.put("Internal GL Version", glGetString(GL_VERSION));
            glinfo.put("GLSL Version", glGetString(GL_SHADING_LANGUAGE_VERSION));

            sysinfo.put("Graphics Renderer", glGetString(GL_RENDERER));
            sysinfo.put("Graphics Vendor", glGetString(GL_VENDOR));
        }else if(renderer.equals("Vulkan")){

        }else{

        }
    }

    public static String get(String value){
        if(glinfo.containsKey(value)) return glinfo.get(value);
        if(sysinfo.containsKey(value)) return sysinfo.get(value);
        if(engineinfo.containsKey(value)) return engineinfo.get(value);
        if(javainfo.containsKey(value)) return javainfo.get(value);
        return "";
    }

    public static String getInfo(){
        String data = "System Information";
        for(String line : sysinfo.keySet()){
            data += "\n" + line + ": " + sysinfo.get(line);
        }

        data += "\n\nJava Information";
        for(String line : javainfo.keySet()){
            data += "\n" + line + ": " + javainfo.get(line);
        }

        data += "\n\nEngine Information";
        for(String line : engineinfo.keySet()){
            data += "\n" + line + ": " + engineinfo.get(line);
        }

        data += "\n\nOpenGL Information";
        for(String line : glinfo.keySet()){
            data += "\n" + line + ": " + glinfo.get(line);
        }
        data += "\n";
        return data;
    }

    private SystemInfo() {
    }

    public static void queryEngineInfo() {
        engineinfo.put("Headless mode", String.valueOf(GGInfo.isServer()));
        engineinfo.put("Application Name", GGInfo.getApplicationName());
        engineinfo.put("Application Path", GGInfo.getApplicationPath().toString());
        engineinfo.put("Default Allocator", GGInfo.getMemoryAllocator());
        engineinfo.put("Aggressive Stack Management", String.valueOf(GGInfo.shouldAggressivelyManageMemory()));
    }
}
