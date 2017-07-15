/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.server;

import com.opengg.core.online.Packet;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

/**
 *
 * @author Javier
 */
public class ServerClient {
    InetAddress ip;
    String name;
    Date timeConnected;
    int latency;
    int port;
    int id;
    
    public void send(DatagramSocket client, byte[] bytes){
        Packet.send(client, bytes, ip, port);
    }
}
