/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network;

import com.opengg.core.console.GGConsole;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Javier
 */
public class Packet implements Serializable{
    private byte[] data;
    private int port;
    private DatagramPacket dp;
    private InetAddress address;
        
    public static Packet receive(DatagramSocket ds, int size){
        var packet = new Packet(size);
        packet.receive(ds);
        return packet;
    }

    public static void sendGuaranteed(DatagramSocket ds, byte[] bytes, InetAddress address, int port){
        send(ds, (byte) 1, bytes, address, port);
    }

    public static void send(DatagramSocket ds, byte[] bytes, InetAddress address, int port){
        send(ds, (byte) 0, bytes, address, port);
    }

    public static void send(DatagramSocket ds, byte type, byte[] bytes, InetAddress address, int port){
        Packet p = new Packet(bytes, address, port);
        p.send(ds);
    }
    
    public byte[] getData(){
        return data;
    }
    
    public int getPort(){
        return port;
    }
    
    public InetAddress getAddress(){
        return address;
    }
    
    private Packet(int size){
        data = new byte[size];
        dp = new DatagramPacket(data, size);
    }
    
    private Packet(byte[] bytes, InetAddress address, int port){
        dp = new DatagramPacket(bytes, bytes.length, address, port);
    }
    
    private void receive(DatagramSocket ds){
        try {
            ds.receive(dp);
            address = dp.getAddress();
            port = dp.getPort();
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
        
}
