/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.server;

import com.opengg.core.console.GGConsole;
import com.opengg.core.network.Packet;
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
    }

    public void start(){
        running = true;
        new Thread(clistener).start();
    }

    public void update(){
        var alltransmitters = WorldEngine.getCurrent().getAllDescendants().stream()
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
            var allcomps = WorldEngine.getCurrent().getAllDescendants();//.stream().filter(s -> new Random().nextInt(5) == 2).collect(Collectors.toList());
            GGOutputStream out = new GGOutputStream();

            out.write(Instant.now().toEpochMilli());
            out.write((short)allcomps.size());

            for(var comp : allcomps){
                out.write((short) comp.getId());
                comp.serializeUpdate(out);
            }

            var bytes = ((ByteArrayOutputStream)out.getStream()).toByteArray();
            for(ServerClient sc : clients){
                sc.send(udpsocket, bytes); }

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

    public void accept(Packet packet){
        var source = getClient(packet.getAddress(), packet.getPort());

        if(source == null){
            for(var client : getClients(packet.getAddress())){
                if(client.getPort() == 0) source = client;
            }
        }

        if(source == null) return;

        source.setLastMessage(Instant.now());

        if(!source.isInitialized()){
            source.setPort(packet.getPort());
            for(int i = 0; i < 10; i++){
                Packet.send(getUDPSocket(), new byte[1024], source.getIp(), source.getPort());
            }

            source.initialize(true);
            GGConsole.log("User at " + source.getIp() +":" + source.getPort() + " connected");

            return;
        }

        if(Arrays.equals(
                packet.getData(),
                ByteBuffer.wrap(new byte[getPacketSize()]).putInt(source.getId()).array()))
            return;

        source.processData(new GGInputStream(packet.getData()));
    }
}
