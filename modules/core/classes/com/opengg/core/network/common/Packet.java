/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.common;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.PerformanceManager;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.util.GGFuture;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Instant;
import java.util.Arrays;
import java.util.BitSet;

/**
 *
 * @author Javier
 */
public class Packet implements Serializable{
    private final int RECEIVER_PACKET_SIZE = 1024*1024;
    private DatagramSocket socket;
    private byte[] data;
    private int length;
    private boolean requestAck;
    private byte type;
    private long timestamp;
    private ConnectionData connectionData;
    private DatagramPacket internalPacket;

    private Packet(DatagramSocket socket, byte type, boolean guarantee, byte[] userBytes, ConnectionData connectionData){
        this.socket = socket;
        this.connectionData = connectionData;
        this.type = type;
        this.length = userBytes.length;
        this.timestamp = Instant.now().toEpochMilli();
        this.requestAck = guarantee;

        try {
            var out = new GGOutputStream();

            byte flags = 0b00000000;
            flags = (byte) (guarantee
                                ? flags | (1 << 0)
                                : flags & ~(1 << 0));

            out.write(flags);
            out.write(type);
            out.write(timestamp);
            out.write(userBytes.length);
            out.write(userBytes);

            var finalBytes = out.asByteArray();

            internalPacket = new DatagramPacket(finalBytes, finalBytes.length, connectionData.address, connectionData.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Packet(){
        data = new byte[RECEIVER_PACKET_SIZE];
        internalPacket = new DatagramPacket(data, RECEIVER_PACKET_SIZE);
    }

    public static Packet receive(){
        var packet = new Packet();
        packet.receivePacket();
        return packet;
    }

    public static GGFuture<Boolean> sendGuaranteed(byte type, byte[] bytes, ConnectionData connectionData){
        Packet p = new Packet(NetworkEngine.getSocket(), type, true, bytes, connectionData);
        var future = new GGFuture<Boolean>();
        NetworkEngine.getPacketReceiver().sendWithAcknowledgement(p, future);
        return future;
    }

    public static void send(byte type, byte[] bytes, ConnectionData connectionData){
        Packet p = new Packet(NetworkEngine.getSocket(), type, false, bytes, connectionData);
        p.send();
    }

    public static void sendAcknowledgement(Packet packet, ConnectionData connectionData){
        try {
            var stream = new GGOutputStream();
            stream.write(packet.getTimestamp());
            var data = stream.asByteArray();

            Packet p = new Packet(NetworkEngine.getSocket(), PacketType.ACK,false, data, connectionData);
            p.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receivePacket(){
        try {
            NetworkEngine.getSocket().receive(internalPacket);
            var address = internalPacket.getAddress();
            var port = internalPacket.getPort();
            connectionData = ConnectionData.get(address,port);
            length = internalPacket.getLength();
            data = Arrays.copyOf(data, length);

            var in = new GGInputStream(data);

            var flags = BitSet.valueOf(in.readByteArray(1));
            requestAck = flags.get(0);

            type = in.readByte();
            timestamp = in.readLong();
            int userLength = in.readInt();
            data = in.readByteArray(userLength);

            PerformanceManager.registerPacketIn(this.length);

        } catch (IOException ex) {
            GGConsole.warning("Failed to receive messages!");
        }
    }

    public void send(){
        try {
            socket.send(internalPacket);
            PerformanceManager.registerPacketOut(this.length);
            if(this.requestsAcknowledgement())
                PerformanceManager.registerGuaranteedPacketSent();
        } catch (IOException ex) {
            GGConsole.warning("Failed to communicate with " + connectionData.address.getHostAddress());
        }
    }

    public byte[] getData(){
        return data;
    }

    public int getLength() {
        return length;
    }

    public byte getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean requestsAcknowledgement() {
        return requestAck;
    }

    public ConnectionData getConnection(){
        return connectionData;
    }
}
