/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online;

import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Javier
 */
public class Client {
    public String servIP;
    public String servName;
    
    public Date timeConnected;
    public Socket socket;
    public int iport, oport;
    public int latency;
    
    public Client(Socket s, String ip, int port){
        this.servIP = ip;
        this.socket = s;
        this.iport = port;
        this.oport = port + 1;
        this.timeConnected = Calendar.getInstance().getTime();
    }
}
