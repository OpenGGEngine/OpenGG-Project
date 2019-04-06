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
import com.opengg.core.network.ConnectionData;
import com.opengg.core.network.Packet;
import com.opengg.core.network.PacketType;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.Component;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class Client {
    private String servName;

    private ConnectionData connectionData;
    private Instant timeConnected;
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private long latency;
    private long timedifference;
    private int packetsize;
    private boolean running;

    private ActionQueuer queuer;
    
    public Client(Socket tcp, DatagramSocket udp, ConnectionData connectionData){
        this.tcpSocket = tcp;
        this.udpSocket = udp;
        this.connectionData = connectionData;
        this.timeConnected = Instant.now();
        this.queuer = ActionQueuer.get();
    }

    public void start(){
        running = true;
    }

    public void doHandshake() throws IOException{
        var in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        var out = new PrintWriter(new OutputStreamWriter(tcpSocket.getOutputStream()), true);

        out.println("hey server");
        var handshake = in.readLine();

        if (!handshake.equals("hey client")) {
            GGConsole.warning("Failed to connect to " + tcpSocket.getInetAddress().getHostAddress() + ", invalid handshake");
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
            Packet.send(udpSocket, PacketType.HANDSHAKE_MESSAGE, bb.array(), connectionData);
            try{
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void getData() throws IOException {
        var in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

        int worldsize = Integer.decode(in.readLine());
        tcpSocket.getOutputStream().write(1);

        GGConsole.log("Downloading world (" + worldsize + " bytes)");

        byte[] bytes = new byte[worldsize];
        tcpSocket.getInputStream().read(bytes);

        var buffer = ByteBuffer.wrap(bytes);

        World w = Deserializer.deserialize(buffer);
        WorldEngine.useWorld(w);

        GGConsole.log("World downloaded");
        GGConsole.log("Connected to " + tcpSocket.getInetAddress());
    }

    public void update(){
        try {
            var out = new GGOutputStream();

            out.write(MouseController.get().multiply(Configuration.getFloat("sensitivity")));
            queuer.writeData(out);

            var data = ((ByteArrayOutputStream)out.getStream()).toByteArray();
            Packet.sendGuaranteed(udpSocket, PacketType.CLIENT_ACTION_UPDATE, data, connectionData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accept(Packet packet){
        byte[] bytes = packet.getData();
        var in = new GGInputStream(bytes);

        OpenGG.asyncExec(() -> {
            try {
                short amount = in.readShort();
                var delta = (Instant.now().toEpochMilli() - (packet.getTimestamp() + timedifference)) / 1000f;
                for (int i = 0; i < amount; i++) {
                    short id = in.readShort();

                    var component = WorldEngine.getCurrent().find(id);

                    if (component != null) {
                        component.deserializeUpdate(in, delta);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void acceptNewComponents(Packet packet) {
        try {
            var in = new GGInputStream(packet.getData());
            var compAmount = in.readInt();
            var loadedCompList = new ArrayList<Deserializer.SerialHolder>();
            OpenGG.asyncExec(() -> {
                try {
                    for (int i = 0; i < compAmount; i++) {
                        loadedCompList.add(Deserializer.deserializeSingleComponent(in));
                    }

                    for (var comp : loadedCompList) {
                        if (comp.parent == 0) WorldEngine.getCurrent().attach(comp.comp);
                        if (WorldEngine.getCurrent().find(comp.parent) != null)
                            WorldEngine.getCurrent().find(comp.parent).attach(comp.comp);
                        loadedCompList.stream()
                                .filter(c -> c.comp.getId() == comp.parent)
                                .findFirst()
                                .ifPresent(c -> c.comp.attach(comp.comp));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptRemovedComponent(Packet packet) {
        try {
            var in = new GGInputStream(packet.getData());
            var amount = in.readInt();
            for(int i = 0; i < amount; i++){
                var comp = in.readInt();
                WorldEngine.markForRemoval(WorldEngine.getCurrent().find(comp));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public ConnectionData getConnection() {
        return connectionData;
    }

    public String getServerName() {
        return servName;
    }

    public Instant getTimeConnected() {
        return timeConnected;
    }

    public Socket getTcpSocket() {
        return tcpSocket;
    }

    public DatagramSocket getUdpSocket() {
        return udpSocket;
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
