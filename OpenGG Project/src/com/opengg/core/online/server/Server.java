/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.server;

import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Server {
    String name;
    ServerSocket socket;
    DatagramSocket dsocket;
    List<ServerClient> clients;
    ConnectionListener clistener;
    ServerThread sthread;
    ServerResponseThread srthread;
    int port;
    int packetsize = 1024;
    
    public Server(String name, int port, ServerSocket ssocket, DatagramSocket dsocket){
        this.name = name;
        this.port = port;
        this.clients = new ArrayList<>();
        this.socket = ssocket;
        this.dsocket = dsocket;
        this.clistener = new ConnectionListener(socket, this);
        this.sthread = new ServerThread(this, 15);
        new Thread(clistener).start();
        new Thread(sthread).start();
        new Thread(srthread).start();
    }
    
    
    
    public void addServerClient(ServerClient sc){
        clients.add(sc);
    }
}
