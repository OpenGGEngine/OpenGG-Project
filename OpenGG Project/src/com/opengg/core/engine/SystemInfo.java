/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class SystemInfo {
    static String glVersion;
    static String os, arch, version, cpu, totmem, pcname;
    static String jre, jrever, jrevend;  
    static long maxmem, usedmem, freemem;
    static int availablecpus, maxcpus;
    static boolean glInit;
    static String gpu, monitormode, nativemode, vram, dvram, svram;
    
    private static boolean dxdiag = false;
    
    public static void querySystemInfo(){
        maxmem = Runtime.getRuntime().maxMemory();
        usedmem = Runtime.getRuntime().totalMemory();
        freemem = Runtime.getRuntime().freeMemory();
        
        availablecpus = Runtime.getRuntime().availableProcessors();
        maxcpus = Integer.parseInt(System.getenv("NUMBER_OF_PROCESSORS"));
        
        os = "Operating System: " + System.getProperty("os.name");
        arch = "OS Architecture: " + System.getProperty("os.arch");
        cpu = "CPU Name: " + System.getenv("PROCESSOR_IDENTIFIER");
        
        jre = System.getProperty("java.vm.name");
        jrever = System.getProperty("java.version");
        jrevend= System.getProperty("java.vm.vendor");
        genDxDiag();
        if(dxdiag){
            try (BufferedReader br = new BufferedReader(new FileReader("dxdiag.txt"));){
                String line;
                while((line = br.readLine()) != null){
                    if(line.trim().startsWith("Operating System:"))
                        os = line.trim();
                    if(line.trim().startsWith("Processor:"))
                        cpu = line.trim();
                    if(line.trim().startsWith("Card name:"))
                        gpu = line.trim();
                    if(line.trim().startsWith("Current Mode:"))
                        monitormode = line.trim();
                    if(line.trim().startsWith("Native Mode:"))
                        nativemode = line.trim();
                    if(line.trim().startsWith("Display Memory:"))
                        vram = line.trim();
                    if(line.trim().startsWith("Dedicated Memory:"))
                        dvram = line.trim();
                    if(line.trim().startsWith("Shared Memory:"))
                        svram = line.trim();
                    if(line.trim().startsWith("Memory:"))
                        totmem = line.trim();
                    if(line.trim().startsWith("System Model:"))
                        pcname = line.trim();
                }
            } catch (IOException ex) {
                GGConsole.warning("Failed to open generated DxDiag file, Java information may be inaccurate");
            }
        }
        
    }
    
    public static void queryOpenGLInfo(){
        glVersion = RenderEngine.getGLVersion();
        if(!(glVersion.equals("") || glVersion == null)){
            glInit = true;
        }
    }
    
    public static String getInfo(){
        String data = "OpenGG Engine Version: " + OpenGG.version;
        
        data += "\n\nSystem Information";
        data += "\n" + pcname;
        data += "\n" + os;
        data += "\n" + pcname;
        data += "\n" + cpu;
        data += "\n" + arch;
        data += "\nCPU Count" + maxcpus;
        data += "\n" + totmem;
        
        data += "\n\nGraphics Information";
        data += "\n" + gpu;
        data += "\n" + monitormode;
        data += "\n" + nativemode;
        data += "\n" + vram;
        data += "\n" + dvram;
        data += "\n" + svram;
        
        data += "\n\nJava Information";
        data += "\nJava Version: " + jrever;
        data += "\nJVM Name: " + jre;
        data += "\nJVM Vendor: " + jrevend;
        data += "\nJVM CPU Availability: " + maxcpus;
        data += "\nCurrent Memory Usage: " + usedmem;
        data += "\nMaximum Available Memory: " + maxmem;
        data += "\nFree Memory: " + freemem;
        
        data += "\n\nOpenGL Information";
        data += "\nOpenGL Initialization State: " + (glInit ? "Initialized successfully" : "Initialization failed");
        data += "\nOpenGL Version: " + glVersion;

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
            GGConsole.warning("DxDiag does not exist or is cannot be accessed, Java information may be inaccurate");
        }
        
    }
}
