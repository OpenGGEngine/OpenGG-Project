/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import java.util.Calendar;

/**
 *
 * @author Javier
 */
public class Message {
    String message;
    String sender;
    String time;
    
    public Message(String message, Class sender){
        this.message = message;
        this.sender = sender.getSimpleName();
        this.time = Calendar.getInstance().getTime().toString();
    }
    
    public Message(String message, String sender){
        this.message = message;
        this.sender = sender;
        this.time = Calendar.getInstance().getTime().toString();
    }
    
    @Override
    public String toString(){
        return "[" + time + "]: " + message;
    }
}
