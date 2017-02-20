/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.client;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.online.Packet;

/**
 *
 * @author Javier
 */
public class ClientResponseThread implements Runnable{
    Client client;
    boolean end = false;
    
    public ClientResponseThread(Client client){
        this.client = client;
        ActionQueuer.initialize(client);
    }
    
    @Override
    public void run() {
        while(!end && !OpenGG.getEnded()){
            try {
                Thread.sleep(1000/30);
            } catch (InterruptedException ex) {
                GGConsole.error("Response Thread failed!");
            }
            
            byte[] actions = ActionQueuer.generatePacket();
            Packet.send(client.udpsocket, actions, client.servIP, client.port);
        }
    }
    
}
