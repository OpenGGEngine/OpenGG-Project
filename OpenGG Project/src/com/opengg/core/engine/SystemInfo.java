/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author Javier
 */
public class SystemInfo {
    static LinkedHashMap<String, String> glinfo = new LinkedHashMap<>();
    static LinkedHashMap<String, String> javainfo = new LinkedHashMap<>();
    static LinkedHashMap<String, String> sysinfo = new LinkedHashMap<>();
    static LinkedHashMap<String, String> engineinfo = new LinkedHashMap<>();
    
    private static boolean dxdiag = false;
    
    public static void querySystemInfo(){
        javainfo.put("Java Version", "" +System.getProperty("java.version"));
        javainfo.put("JVM Name", "" +System.getProperty("java.vm.name"));
        javainfo.put("JVM Vendor", ""+System.getProperty("java.vm.vendor"));
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
                        info.put(os, line.trim());
                    if(line.trim().startsWith("Processor:"))
                        info.put(cpu, line.trim());
                    if(line.trim().startsWith("Card name:"))
                        info.put(gpu, line.trim());
                    if(line.trim().startsWith("Current Mode:"))
                        info.put(monitormode, line.trim());
                    if(line.trim().startsWith("Native Mode:"))
                        info.put(nativemode, line.trim());
                    if(line.trim().startsWith("Display Memory:"))
                        info.put(vram, line.trim());
                    if(line.trim().startsWith("Dedicated Memory:"))
                        info.put(dvram, line.trim());
                    if(line.trim().startsWith("Shared Memory:"))
                        info.put(svram, line.trim());
                    if(line.trim().startsWith("Memory:"))
                        info.put(totmem, line.trim());
                    if(line.trim().startsWith("System Model:"))
                        info.put(pcname, line.trim());
                }
            } catch (IOException ex) {
                GGConsole.warning("Failed to open generated DxDiag file, Java information may be inaccurate");
            }
        }
                */
        
    }
    
    public static void queryOpenGLInfo(){
        String glVersion = RenderEngine.getGLVersion();
        glinfo.put("GL Version", glVersion);
        glinfo.put("GL Initialization", !(glVersion == null || glVersion.equals("")) ? "Initialized successfully" : "Initialization failed");
    }
    
    public static void queryEngineInfo(){
        
    }
    
    public static String getInfo(){
        String data = "OpenGG Engine Version: " + OpenGG.version;
        
        data += "\n\nSystem Information";
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
    
    public static void genDxDiag(){
        String filePath = "dxdiag.txt";
        ProcessBuilder pb = new ProcessBuilder("cmd.exe","/c","dxdiag","/t",filePath);
        Process p;
        try {
            p = pb.start();
            p.waitFor();
            dxdiag = true;
        } catch (IOException | InterruptedException ex) {
            GGConsole.warning("DxDiag does not exist or cannot be accessed, Java information may be inaccurate");
        }
        
    }
}
