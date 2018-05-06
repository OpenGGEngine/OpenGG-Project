/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.server;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Vector3f;
import com.opengg.core.network.Packet;
import com.opengg.core.network.client.ActionQueuer;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.ActionTransmitterComponent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

    private NewConnectionListener clistener;
    private ServerResponseThread serverresponsethread;

    private boolean running;
    private int port;
    private final int packetsize = 2048;

    private List<ConnectionListener> listeners;
    
    public Server(String name, int port, ServerSocket ssocket, DatagramSocket dsocket){
        this.name = name;
        this.port = port;
        this.clients = new ArrayList<>();
        this.newclients = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.tcpsocket = ssocket;
        this.udpsocket = dsocket;
        this.clistener = new NewConnectionListener(this);
        this.serverresponsethread = new ServerResponseThread(this);
    }

    public void start(){
        running = true;
        new Thread(clistener).start();
        new Thread(serverresponsethread).start();
    }


    public void update(){
        var alltransmitters = WorldEngine.getCurrent().getAll().stream()
                .filter(c -> c instanceof ActionTransmitterComponent)
                .map(c -> (ActionTransmitterComponent)c)
                .collect(Collectors.toList());

        for(var client : clients){
            for(var transmitter : alltransmitters){
                if(transmitter.getUserId() == client.getId() && !client.getTransmitters().contains(transmitter)){
                    client.getTransmitters().add(transmitter);
                }
            }
        }

        var removal = new ArrayList<ServerClient>();

        for(var client : this.clients){
            if(client.getLastMessage() != null && Instant.now().toEpochMilli() - client.getLastMessage().toEpochMilli() > 5000){
                removal.add(client);
                GGConsole.log(client.getIp().getHostAddress() + " has timed out");

                //todo
            }
        }

        clients.removeAll(removal);

        clients.addAll(newclients);
        newclients.clear();
        sendState();
    }

    public void sendState(){
        try {
            var allcomps = WorldEngine.getCurrent().getAll();
            GGOutputStream out = new GGOutputStream();

            out.write(Instant.now().toEpochMilli());
            out.write((short)allcomps.size());

            for(var comp : allcomps){
                out.write((short) comp.getId());
                comp.serializeUpdate(out);
            }

            var bytes = ((ByteArrayOutputStream)out.getStream()).toByteArray();
            for(ServerClient sc : clients){ sc.send(udpsocket, bytes); }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerClient getClient(InetAddress ip, int port){
        var tempclients =  List.copyOf(clients);
        return tempclients.stream()
                .filter(client -> client.getIp().equals(ip))
                .filter(client -> client.getPort() == port)
                .findFirst().orElse(null);
    }

    public List<ServerClient> getClients(InetAddress ip){
        var tempclients =  List.copyOf(clients);
        return tempclients.stream()
                .filter(client -> client.getIp().equals(ip))
                .collect(Collectors.toList());
    }

    public ServerClient getByID(int id){
        for(var client : clients){
            if(client.getId() == id) return client;
        }

        return null;
    }

    public void subscribe(ConnectionListener listener){
        listeners.add(listener);
    }

    public void addServerClient(ServerClient sc){
        newclients.add(sc);

        for(var listener : listeners)
            listener.onConnection(sc);
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
            while(server.running && !OpenGG.getEnded()){
                packet = Packet.receive(server.udpsocket, server.packetsize);

                var source = server.getClient(packet.getAddress(), packet.getPort());

                if(source == null){
                    for(var client : server.getClients(packet.getAddress())){
                        if(client.getPort() == 0) source = client;
                    }
                }

                if(source == null) continue;

                source.setLastMessage(Instant.now());

                if(!source.isInitialized()){
                    source.setPort(packet.getPort());
                    for(int i = 0; i < 10; i++){
                        Packet.send(server.udpsocket, new byte[1024], source.getIp(), source.getPort());
                    }

                    source.initialize(true);
                    GGConsole.log("User at " + source.getIp() +":" + source.getPort() + " connected");

                    continue;
                }

                if(Arrays.equals(packet.getData(),  ByteBuffer.wrap(new byte[server.packetsize]).putInt(source.getId()).array())) continue;

                source.processData(new GGInputStream(packet.getData()));
            }
        }
    }
}
