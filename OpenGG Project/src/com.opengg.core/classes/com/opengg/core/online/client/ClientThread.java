/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.client;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.online.NetworkSerializer;
import com.opengg.core.online.Packet;

/**
 *
 * @author Javier
 */
public class ClientThread implements Runnable{
    Client c;
    boolean end = false;
    
    public ClientThread(Client c){
        this.c = c;
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            GGConsole.error("Response Thread failed!");
        }
        while(!OpenGG.getEnded()){
            Packet p = Packet.receive(c.udpsocket, c.packetsize);
            byte[] bytes = p.getData();
            NetworkSerializer.deserializeUpdate(bytes);
        }
    }
    
}
