/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.server;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.online.Packet;

/**
 *
 * @author Javier
 */
public class ServerResponseThread implements Runnable{
    Server s;
    Packet p;
    boolean end = false;
    
    public ServerResponseThread(Server s){
        this.s = s;
    }

    @Override
    public void run() {
        while(!end && !OpenGG.getEnded()){
            p = Packet.receive(s.dsocket, s.packetsize);
        }
    }
}
