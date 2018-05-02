/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.server;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.online.NetworkSerializer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class ServerThread implements Runnable{
    Server s;
    boolean end = false;
    int packetpersecond;

    public ServerThread(Server s, int packetpersecond){
        this.s = s;
        this.packetpersecond = packetpersecond;
    }
    
    @Override
    public void run() {
        while(!OpenGG.getEnded()){
            byte[] bytes = NetworkSerializer.serializeUpdate(WorldEngine.getCurrent().getAll());

            for(ServerClient sc : s.clients){
                sc.send(s.dsocket, bytes);
            }
            
            try {
                Thread.sleep(1000/packetpersecond);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
