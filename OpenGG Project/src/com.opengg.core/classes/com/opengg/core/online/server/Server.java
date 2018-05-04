/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.server;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.online.NetworkSerializer;
import com.opengg.core.online.Packet;
import com.opengg.core.world.WorldEngine;

import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Server {
    private String name;
    private ServerSocket tcpsocket;
    private DatagramSocket udpsocket;
    private List<ServerClient> clients;
    private List<ServerClient> newclients;

    private ConnectionListener clistener;
    private ServerThread serverthread;
    private ServerResponseThread serverresponsethread;

    private boolean running;
    private int port;
    private int packetsize = 1024;
    
    public Server(String name, int port, ServerSocket ssocket, DatagramSocket dsocket){
        this.name = name;
        this.port = port;
        this.clients = new ArrayList<>();
        this.newclients = new ArrayList<>();
        this.tcpsocket = ssocket;
        this.udpsocket = dsocket;
        this.clistener = new ConnectionListener(this);
        this.serverthread = new ServerThread(this, 15);
        this.serverresponsethread = new ServerResponseThread(this);
    }

    public void start(){
        running = true;
        new Thread(clistener).start();
        new Thread(serverthread).start();
        new Thread(serverresponsethread).start();
    }

    public void addServerClient(ServerClient sc){
        newclients.add(sc);
    }

    public String getName() {
        return name;
    }

    public ServerSocket getTCPSocket() {
        return tcpsocket;
    }

    public DatagramSocket getUDPSocket() {
        return udpsocket;
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }

    public int getPacketSize() {
        return packetsize;
    }

    /**
     *
     * @author Javier
     */
    private static class ServerResponseThread implements Runnable{
        private Server server;
        private Packet packet;

        public ServerResponseThread(Server s){
            this.server = s;
        }

        @Override
        public void run() {
            while(!server.running && !OpenGG.getEnded()){
                packet = Packet.receive(server.udpsocket, server.packetsize);
            }
        }
    }

    /**
     *
     * @author Javier
     */
    private static class ServerThread implements Runnable{
        Server server;
        int bandwidth;

        public ServerThread(Server s, int bandwidth){
            this.server = s;
            this.bandwidth = bandwidth;
        }

        @Override
        public void run() {
            while(server.running && !OpenGG.getEnded()){
                //byte[] bytes = NetworkSerializer.serializeUpdate(WorldEngine.getCurrent().getAll());

                for(ServerClient sc : server.clients){
                    sc.send(server.udpsocket, new byte[server.packetsize]);
                }

                server.clients.addAll(server.newclients);
                server.newclients.clear();

                try {
                    Thread.sleep(1/bandwidth);
                } catch (InterruptedException ex) {
                    GGConsole.error("Server thread interrupted!");
                }
            }
        }
    }
}
