/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.client;

import com.opengg.core.engine.BindController;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionTransmitter;
import com.opengg.core.world.ActionType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ActionQueuer implements ActionTransmitter{
    private long lastTime;
    private Client client;
    private List<ActionContainer> actions = Collections.synchronizedList(new ArrayList<>());
    
    private ActionQueuer(){
        BindController.addController(this);
    }
    
    public static ActionQueuer get(Client client){
        var queuer = new ActionQueuer();
        queuer.client = client;
        queuer.lastTime = Instant.now().toEpochMilli();
        return queuer;
    }

    public byte[] generatePacket(){
        var out = new GGOutputStream();
        try {
            out.write(lastTime);

            out.write(actions.size());
            for(ActionContainer action : actions){
                out.write(action.action.name);
                out.write(action.action.type.name());
            }

            actions.clear();
            lastTime = Instant.now().toEpochMilli();
            return ((ByteArrayOutputStream)out.getStream()).toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static List<Action> getFromPacket(byte[] data){
        var in = new GGInputStream(new ByteArrayInputStream(data));
        try {
            long lasttime = in.readLong();

            int actionsize = in.readInt();
            var actions = new ArrayList<Action>(actionsize);
            for(int i = 0; i < actionsize; i++){
                var action = new Action();
                var type = in.readString();

                switch(type){
                    case "PRESS":
                        action.type = ActionType.PRESS;
                        break;
                    case "RELEASE":
                        action.type = ActionType.RELEASE;
                        break;
                }

                actions.add(action);
            }

            return actions;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void doAction(Action action) {
        ActionContainer actcont = new ActionContainer();
        actcont.action = action;
        actcont.delta = Instant.now().toEpochMilli() - lastTime;
        actions.add(actcont);
    }
}
