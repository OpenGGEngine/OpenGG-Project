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
public class GGMessage {
    String message;
    String sender;
    String time;
    Level level;
    
    public GGMessage(String message, Class sender, Level level){
        this.message = message;
        this.sender = sender.getSimpleName();
        this.level = level;
        this.time = Calendar.getInstance().getTime().toString();
    }
    
    public GGMessage(String message, String sender, Level level){
        this.message = message;
        this.sender = sender;
        this.level = level;
        this.time = Calendar.getInstance().getTime().toString();
    }
    
    @Override
    public String toString(){
        return "[" + time + "] " + level + " (" + sender + "): " + message;
    }
}
