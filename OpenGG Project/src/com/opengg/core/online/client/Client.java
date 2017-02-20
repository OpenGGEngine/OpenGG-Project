/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.client;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Javier
 */
public class Client {
    public InetAddress servIP;
    public String servName;
    
    public Date timeConnected;
    public DatagramSocket udpsocket;
    public int port;
    public int latency;
    public int packetsize;
    
    public ClientThread input;
    public ClientResponseThread output;
    
    public Client(DatagramSocket ds, InetAddress ip, int port, String servName, int packetsize){
        this.servIP = ip;
        this.udpsocket = ds;
        this.port = port;
        this.servName = servName;
        this.packetsize = packetsize;
        this.timeConnected = Calendar.getInstance().getTime();
        this.input = new ClientThread(this);
        this.output = new ClientResponseThread(this);
        new Thread(input).start();
        new Thread(output).start();
    }
}
