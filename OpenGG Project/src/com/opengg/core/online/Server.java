/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online;

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
    List<ServerClient> clients;
    ConnectionListener clistener;
    int iport, oport;
    
    
    public Server(String name, int port, ServerSocket ssocket){
        this.name = name;
        this.iport = port;
        this.oport = port + 1;
        this.clients = new ArrayList<>();
        this.socket = ssocket;
        ConnectionListener listener = new ConnectionListener(socket, this);
        new Thread(listener).start();
    }
    
    public void addServerClient(ServerClient sc){
        clients.add(sc);
    }
}
