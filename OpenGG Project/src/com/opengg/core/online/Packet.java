/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online;

import com.opengg.core.engine.GGConsole;
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
    byte[] buf;
    int port;
    DatagramPacket dp;
    InetAddress address;
        
    public static Packet receive(DatagramSocket ds, int size){
        Packet p = new Packet(size);
        p.receive(ds);
        return p;
    }
    
    public byte[] getData(){
        return buf;
    }
    
    public int getPort(){
        return port;
    }
    
    public static void send(DatagramSocket ds, byte[] bytes, InetAddress address, int port){
        Packet p = new Packet(bytes, address, port);
        p.send(ds);
    }
    
    private Packet(int size){
        buf = new byte[size];
        dp = new DatagramPacket(buf, size);
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
