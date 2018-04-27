/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.console;

import com.opengg.core.GGInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class GGConsole implements Runnable{
    private static final List<GGMessage> messages = new LinkedList<>();
    private static final List<ConsoleListener> listeners = new ArrayList<>();
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static boolean running = true;
    
    public static List<GGMessage> getAllMessages(){
        return messages;
    }
    
    public static GGMessage getMostRecent(){
        return messages.get(messages.size()-1);
    }
    
    public static void log(String message){
        write(message, Level.INFO);
    }
    
    public static void logVerbose(String message){
        if(GGInfo.isVerbose())
            write(message, Level.INFO);
    }
            
    public static void warning(String message){
        write(message, Level.WARNING);
    }
    
    public static void warn(String message){
        write(message, Level.WARNING);
    }
    
    public static void error(String message){
        write(message, Level.ERROR);
    }
    
    
    private static void write(String message, Level level){
        GGMessage m = new GGMessage(message, getSender(), level);
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
    
    public static void writeLog(Date date){
        String dates = DateFormat.getDateTimeInstance().format(date);
        dates = dates.replace(":", "-");
        writeLog(date, "", dates);
    }
    
    public static void writeLog(Date date, String error, String name){
        try(PrintWriter writer = new PrintWriter(new FileOutputStream(new File(new File("").getCanonicalPath()+("logs" + File.separator + name + ".log"))))) {
            for(GGMessage m : messages){
                writer.println(m.toString());
            }
            writer.println(error);
        } catch (IOException ex) {
            GGConsole.error("Could not create log file!");
        }
    }

    @Override
    public void run() {
        try{
            while(!Thread.interrupted()){
                if(in.ready()){
                    String s = "d";
                    try {s = in.readLine();} catch (IOException ex) {}
                    UserCommand command = new UserCommand();
                    command.time = Calendar.getInstance().getTime();
                    String[] strings = s.split(" ");
                    command.command = strings[0];
                    command.argCount = strings.length - 1;
                    command.args = Arrays.copyOfRange(strings, 1, strings.length);
                    for(ConsoleListener listener : listeners){
                        listener.onConsoleInput(command);
                    }
                }
                Thread.sleep(5);
            }
        }catch(IOException e){
            GGConsole.error("Console failed to access the default input, thread will be forced to close");
        } catch (InterruptedException ex) {
            GGConsole.log("Console thread closure requested");
        }
    }
}
