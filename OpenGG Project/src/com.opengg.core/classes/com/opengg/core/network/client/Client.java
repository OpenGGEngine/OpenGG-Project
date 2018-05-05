/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.client;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.network.Packet;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;

/**
 *
 * @author Javier
 */
public class Client {
    private InetAddress address;
    private String servName;
    
    private Instant timeConnected;
    private Socket tcpsocket;
    private DatagramSocket udpsocket;
    private int port;
    private int latency;
    private int packetsize;
    private boolean running;
    
    private ClientThread input;
    private ClientResponseThread output;
    
    public Client(Socket tcp, DatagramSocket udp, InetAddress ip, int port){
        this.tcpsocket = tcp;
        this.udpsocket = udp;
        this.address = ip;
        this.port = port;
        this.timeConnected = Instant.now();
        this.input = new ClientThread(this);
        this.output = new ClientResponseThread(this);
    }

    public void start(){
        new Thread(input).start();
        new Thread(output).start();
        running = true;
    }

    public void doHandshake() throws IOException{
        var in = new BufferedReader(new InputStreamReader(tcpsocket.getInputStream()));
        var out = new PrintWriter(new OutputStreamWriter(tcpsocket.getOutputStream()), true);

        out.println("hey server");
        var handshake = in.readLine();

        if (!handshake.equals("hey client")) {
            GGConsole.warning("Failed to connect to " + tcpsocket.getInetAddress().getHostAddress() + ", invalid handshake");
        }

        out.println("oh shit we out here");
        servName = in.readLine();
        out.println(OpenGG.getApp().applicationName);

        packetsize = Integer.decode(in.readLine());
        int id = Integer.decode(in.readLine());

        GGInfo.setUserId(id);
    }

    public void udpHandshake(){
        for(int i = 0; i < 20; i++){
            Packet.send(udpsocket, new byte[packetsize], address, port);
            try{
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void getData() throws IOException {
        var in = new BufferedReader(new InputStreamReader(tcpsocket.getInputStream()));

        int worldsize = Integer.decode(in.readLine());
        byte[] bytes = new byte[worldsize];

        tcpsocket.getInputStream().read(bytes);

        World w = Deserializer.deserialize(ByteBuffer.wrap(bytes));
        WorldEngine.useWorld(w);
    }


    public InetAddress getServerIP() {
        return address;
    }

    public String getServerName() {
        return servName;
    }

    public Instant getTimeConnected() {
        return timeConnected;
    }

    public int getPort() {
        return port;
    }

    private static class ClientThread implements Runnable{
        Client client;

        public ClientThread(Client client){
            this.client = client;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                GGConsole.error("Response Thread failed!");
            }

            while(client.running && !OpenGG.getEnded()){
                Packet p = Packet.receive(client.udpsocket, client.packetsize);
                byte[] bytes = p.getData();

                if(Arrays.equals(bytes, new byte[client.packetsize])) continue;


                //NetworkSerializer.deserializeUpdate(bytes);
            }
        }
    }

    private static class ClientResponseThread implements Runnable{
        Client client;
        ActionQueuer queuer;

        public ClientResponseThread(Client client){
            this.client = client;
            queuer = ActionQueuer.get(client);
        }

        @Override
        public void run() {
            while(client.running && !OpenGG.getEnded()){
                byte[] actions = queuer.generatePacket();

                Packet.send(client.udpsocket, actions, client.address, client.port);

                try {
                    Thread.sleep(1000/20);
                } catch (InterruptedException ex) {
                    GGConsole.error("Response Thread failed!");
                }
            }
        }

    }
}
