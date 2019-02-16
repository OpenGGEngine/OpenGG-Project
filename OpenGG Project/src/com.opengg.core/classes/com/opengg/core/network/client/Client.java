/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.client;

import com.opengg.core.Configuration;
import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.network.Packet;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Instant;

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
    private long latency;
    private long timedifference;
    private int packetsize;
    private boolean running;

    private ActionQueuer queuer;
    
    public Client(Socket tcp, DatagramSocket udp, InetAddress ip, int port){
        this.tcpsocket = tcp;
        this.udpsocket = udp;
        this.address = ip;
        this.port = port;
        this.timeConnected = Instant.now();
        this.queuer = ActionQueuer.get();
    }

    public void start(){
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

        var start = Instant.now();
        out.println("Sonic");

        in.readLine();
        var end = Instant.now();

        latency = end.toEpochMilli() - start.toEpochMilli();

        var time = in.readLine();
        var longtime = Long.decode(time) + (latency/2);

        timedifference = end.toEpochMilli() - longtime;

        GGInfo.setUserId(id);
    }

    public void udpHandshake(){
        for(int i = 0; i < 5; i++){
            var bb = ByteBuffer.wrap(new byte[packetsize]).putInt(GGInfo.getUserId());
            Packet.send(udpsocket, bb.array(), address, port);
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
        tcpsocket.getOutputStream().write(1);

        GGConsole.log("Downloading world (" + worldsize + " bytes)");

        byte[] bytes = new byte[worldsize];
        tcpsocket.getInputStream().read(bytes);

        var buffer = ByteBuffer.wrap(bytes);

        World w = Deserializer.deserialize(buffer);
        WorldEngine.useWorld(w);

        GGConsole.log("World downloaded");
    }

    public void update(){
        try {
            var out = new GGOutputStream();

            out.write(MouseController.get().multiply(Configuration.getFloat("sensitivity")));
            queuer.writeData(out);

            var data = ((ByteArrayOutputStream)out.getStream()).toByteArray();
            Packet.send(udpsocket, data, address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accept(Packet packet){
        byte[] bytes = packet.getData();
        var in = new GGInputStream(bytes);
        try {

            long time = in.readLong() + timedifference;
            short amount = in.readShort();
            var delta = (Instant.now().toEpochMilli() - time)/1000f;
            for (int i = 0; i < amount; i++) {
                short id = in.readShort();
                var component = WorldEngine.getCurrent().find(id);

                if(component != null){
                    component.deserializeUpdate(in);
                    component.update(delta);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public long getLatency(){
        return latency;
    }

    public boolean isRunning(){
        return running;
    }

    public int getPacketSize(){
        return packetsize;
    }
}
