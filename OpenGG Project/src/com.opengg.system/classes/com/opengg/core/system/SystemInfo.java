/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.system;

import com.opengg.core.console.GGConsole;
import com.opengg.core.GGInfo;
import java.io.IOException;
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
    
    public static void queryOpenGLInfo(){
        String glVersion = GGInfo.getGlVersion();
        glinfo.put("GL Version", glVersion);
        glinfo.put("GL Initialization", !(glVersion == null || glVersion.isEmpty()) ? "Initialized successfully" : "Initialization failed");
    }
    
    public static void queryEngineInfo(){
        
    }
    
    public static String getInfo(){
        String data = "OpenGG Engine Version: " + GGInfo.getVersion();
        
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

    private SystemInfo() {
    }
}
