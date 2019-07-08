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
import com.opengg.core.network.common.ConnectionData;
import com.opengg.core.network.common.Packet;
import com.opengg.core.network.common.PacketType;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.WorldEngine;

import java.io.*;
import java.net.DatagramSocket;
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
    private DatagramSocket udpSocket;
    private long latency;
    private long timedifference;
    private int packetsize;
    private boolean running;

    private ActionQueuer queuer;
    
    public Client(DatagramSocket udp, ConnectionData connectionData){
        this.udpSocket = udp;
        this.connectionData = connectionData;
        this.timeConnected = Instant.now();
        this.queuer = ActionQueuer.get();
    }

    public void start(){
        running = true;
    }

    public void udpHandshake(){
        for(int i = 0; i < 5; i++){
            var bb = ByteBuffer.wrap(new byte[packetsize]).putInt(GGInfo.getUserId());
            //Packet.send(udpSocket, PacketType.HANDSHAKE_MESSAGE, bb.array(), connectionData);
            try{
                Thread.sleep(10);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
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
                    long id = in.readLong();

                    WorldEngine.getCurrent().findByGUID(id)
                            .ifPresentOrElse(c -> {
                                try {
                                    c.deserializeUpdate(in, delta);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }, () -> GGConsole.warning("Failed to find component of id " + id + " during server deserialization"));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void acceptNewComponents(Packet packet) {
            OpenGG.asyncExec(() -> {
                try {
                    var in = new GGInputStream(packet.getData());
                    var compAmount = in.readInt();
                    var loadedCompList = new ArrayList<Deserializer.SerialHolder>();

                    for (int i = 0; i < compAmount; i++) {
                        loadedCompList.add(Deserializer.deserializeSingleComponent(in));
                    }

                    for (var comp : loadedCompList) {
                        WorldEngine.findEverywherByGUID(comp.parent)
                                .ifPresentOrElse(c -> c.attach(comp.comp),
                                        () ->  loadedCompList.stream()
                                                .filter(c -> c.comp.getName().equals(comp.parent))
                                                .findFirst()
                                                .ifPresentOrElse(c -> c.comp.attach(comp.comp),
                                                        () -> GGConsole.warning("Failed to findByName component " + comp.parent + " while deserializing " + comp.comp.getName())));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

    }

    public void acceptRemovedComponent(Packet packet) {
        try {
            var in = new GGInputStream(packet.getData());
            var amount = in.readInt();
            for(int i = 0; i < amount; i++){
                var comp = in.readLong();
                WorldEngine.markComponentForRemoval(WorldEngine.getCurrent().findByGUID(comp).orElseThrow(() -> new IllegalStateException("Failed to find component with id " + comp + " to remove!")));
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
