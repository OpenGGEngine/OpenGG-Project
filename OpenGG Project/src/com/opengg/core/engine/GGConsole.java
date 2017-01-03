/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class GGConsole {
    static List<Message> messages = new LinkedList<>();
    static List<ConsoleListener> listeners = new ArrayList<>();
    static Scanner in = new Scanner(System.in);
    
    public static List<Message> getAllMessages(){
        return messages;
    }
    
    public static Message getMostRecent(){
        return messages.get(messages.size()-1);
    }
    
    public static void log(String message){
        write(message, Level.INFO);
    }
    
    public static void warning(String message){
        write(message, Level.WARNING);
    }
    
    public static void error(String message){
        write(message, Level.ERROR);
    }
    
    
    private static void write(String message, Level level){
        Message m = new Message(message, getSender(), level);
        messages.add(m);
        System.out.println(m);
    } 
    
    private static String getSender(){
        StackTraceElement[] e = Thread.currentThread().getStackTrace();
        return (e[4].getClassName()).substring(e[4].getClassName().lastIndexOf('.')+1);
    }
    
    public static void addListener(ConsoleListener listener){
        listeners.add(listener);
    }
    
    public static void pollInput(){
        if(in.hasNext()){
            String s = in.next();
            listeners.stream().forEach((l) -> {
                l.onConsoleInput(s);
            });
        }
    }
    
    public static void writeLog(Date date){
        try {
            String dates = DateFormat.getDateTimeInstance().format(date);
            dates = dates.replace(":", "-");
            
            Path p = Paths.get(new File("logs\\" + dates + ".log").getCanonicalPath());
            
            List<String> lines = new ArrayList<>();
            messages.stream().forEach((m) -> {
                lines.add(m.toString());
            });
            
            Files.write(p, lines);
        } catch (IOException ex) {
            Logger.getLogger(GGConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    static void destroy(){
        in.close();
    }
}
