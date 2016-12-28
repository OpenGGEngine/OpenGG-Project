/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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
    
    public static void log(String s){
        StackTraceElement[] e = Thread.currentThread().getStackTrace();
        String sender = e[e.length-2].getClassName();
        Message m = new Message(s,sender);
        messages.add(m);
        System.out.println(m);
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
    
    static void destroy(){
        in.close();
    }
}
