/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network;

import com.opengg.core.console.GGConsole;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;
import java.util.Arrays;
import java.util.BitSet;

/**
 *
 * @author Javier
 */
public class Packet implements Serializable{
    private final int RECEIVER_PACKET_SIZE = 1024*1024;

    private byte[] data;
    private int port;
    private int length;
    private boolean requestAck;
    private byte type;
    private long timestamp;
    private DatagramPacket dp;
    private InetAddress address;

    private Packet(DatagramSocket socket, byte type, boolean guarantee, byte[] userBytes, InetAddress address, int port){
        this.address = address;
        this.port = port;
        this.type = type;
        this.length = userBytes.length;
        this.timestamp = Instant.now().toEpochMilli();
        this.requestAck = guarantee;

        try {
            var out = new GGOutputStream();

            /*var set = new BitSet(8);

            set.set(0, guarantee);
            set.set(1, false);
            set.set(2, false);
            set.set(3, false);

            System.out.println(set);*/

            //out.write(set.toByteArray()[0]);
            out.write(type);
            out.write(timestamp);
            out.write(userBytes.length);
            out.write(userBytes);

            var finalBytes = out.asByteArray();

            dp = new DatagramPacket(finalBytes, finalBytes.length, address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Packet(){
        data = new byte[RECEIVER_PACKET_SIZE];
        dp = new DatagramPacket(data, RECEIVER_PACKET_SIZE);
    }

    public static Packet receive(DatagramSocket ds){
        var packet = new Packet();
        packet.receivePacket(ds);
        return packet;
    }

    public static void sendGuaranteed(DatagramSocket ds, byte type, byte[] bytes, InetAddress address, int port){
        Packet p = new Packet(ds, type, true, bytes, address, port);
        p.send(ds);
    }

    public static void send(DatagramSocket ds, byte type, byte[] bytes, InetAddress address, int port){
        Packet p = new Packet(ds, type, false, bytes, address, port);
        p.send(ds);
    }

    public static void sendAcknowledgement(DatagramSocket ds, Packet packet, InetAddress address, int port){
        try {
            var stream = new GGOutputStream();
            stream.write(packet.getTimestamp());
            var data = stream.asByteArray();

            Packet p = new Packet(ds, PacketType.ACK,false, data, address, port);
            p.send(ds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receivePacket(DatagramSocket ds){
        try {
            ds.receive(dp);
            address = dp.getAddress();
            port = dp.getPort();
            length = dp.getLength();
            data = Arrays.copyOf(data, length);

            var in = new GGInputStream(data);

           // var flags = BitSet.valueOf(in.readByteArray(1));
            //requestAck = flags.get(0);
            type = in.readByte();
            timestamp = in.readLong();
            int userLength = in.readInt();
            data = in.readByteArray(userLength);

        } catch (IOException ex) {
            GGConsole.warning("Failed to receive messages!");
        }
    }

    private void send(DatagramSocket ds){
        try {
            ds.send(dp);
        } catch (IOException ex) {
            GGConsole.warning("Failed to communicate with " + address.getHostAddress());
        }
    }

    public byte[] getData(){
        return data;
    }
    
    public int getPort(){
        return port;
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

    public InetAddress getAddress(){
        return address;
    }
}
