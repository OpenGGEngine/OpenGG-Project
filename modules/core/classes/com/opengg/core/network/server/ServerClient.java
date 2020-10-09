/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.server;

import com.opengg.core.math.Vector2f;
import com.opengg.core.network.common.ConnectionData;
import com.opengg.core.network.client.ActionQueuer;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.world.components.ActionTransmitterComponent;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ServerClient {
    private static int idcounter = 0;

    private final ConnectionData connectionData;
    private final String name;
    private final Instant timeConnected;
    private Instant lastMessage;
    private int latency;
    private final int id;
    private boolean success;
    private Vector2f mousepos = new Vector2f();

    private final List<ActionTransmitterComponent> transmitters;

    public ServerClient(String name, ConnectionData data, Instant timeConnected){
        this.name = name;
        this.connectionData = data;
        this.timeConnected = timeConnected;
        this.id = idcounter;
        this.transmitters = new ArrayList<>();
        idcounter++;
    }

    public String getName(){
        return name;
    }

    public Instant getTimeConnected(){
        return timeConnected;
    }

    public ConnectionData getConnection(){
        return connectionData;
    }

    public int getId(){
        return id;
    }

    public boolean receivedFirstMessage(){
        return success;
    }

    public void initialize(boolean done){
        this.success = done;
    }

    public List<ActionTransmitterComponent> getTransmitters(){
        return transmitters;
    }

    public Instant getLastMessage(){
        return lastMessage;
    }

    public void setLastMessage(Instant lastMessage){
        this.lastMessage = lastMessage;
    }

    public Vector2f getMousePosition() {
        return mousepos;
    }

    public void processData(GGInputStream in){
        try {
            mousepos = in.readVector2f();
            var actions = ActionQueuer.getFromPacket(in);

            for (var trans : transmitters) {
                for (var action : actions) {
                    trans.doAction(action);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static int getNextID(){
        return idcounter;
    }

    @Override
    public String toString(){
        return name + " (" + connectionData + ")";
    }
}
