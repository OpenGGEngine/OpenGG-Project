/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.server;

import com.opengg.core.online.Packet;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;
import java.util.Date;

/**
 *
 * @author Javier
 */
public class ServerClient {
    private static int idcounter = 0;

    private InetAddress ip;
    private String name;
    private Instant timeConnected;
    private int latency;
    private int port;
    private int id;

    public ServerClient(String name, InetAddress ip, Instant timeConnected){
        this.name = name;
        this.ip = ip;
        this.timeConnected = timeConnected;
        this.id = idcounter;
        idcounter++;
    }

    public void send(DatagramSocket client, byte[] bytes){
        Packet.send(client, bytes, ip, port);
    }
}
