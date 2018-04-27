/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.client;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.OpenGG;

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
            //byte[] actions = ActionQueuer.generatePacket();
            //Packet.send(client.udpsocket, actions, client.servIP, client.port);
            
            try {
                Thread.sleep(1000/30);
            } catch (InterruptedException ex) {
                GGConsole.error("Response Thread failed!");
            }
        }
    }
    
}
