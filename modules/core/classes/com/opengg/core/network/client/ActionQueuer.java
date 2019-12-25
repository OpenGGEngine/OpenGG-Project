/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.network.client;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.BindController;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionTransmitter;
import com.opengg.core.world.ActionType;

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
    private int currentpacket;
    private List<ActionContainer> actions = Collections.synchronizedList(new ArrayList<>());
    
    private ActionQueuer(){
        BindController.addTransmitter(this);
    }
    
    public static ActionQueuer get(){
        var queuer = new ActionQueuer();
        queuer.currentpacket = 0;
        return queuer;
    }

    public void writeData(GGOutputStream out) throws IOException {

        var tempaction = List.copyOf(actions);

        //out.write(currentpacket);
        out.write(tempaction.size());
        for (ActionContainer action : tempaction) {
            out.write(action.action.name);
            out.write(action.action.type.name());
        }

        actions.removeAll(tempaction);
        currentpacket++;

    }

    public static List<Action> getFromPacket(GGInputStream in){
        try {
            int actionsize = in.readInt();
            var actions = new ArrayList<Action>(actionsize);
            for(int i = 0; i < actionsize; i++){
                var action = new Action();
                var name = in.readString();
                var type = in.readString();

                action.name = name;

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
            GGConsole.error("Failed to load actions");
            return null;
        }
    }

    @Override
    public void doAction(Action action) {
        ActionContainer actcont = new ActionContainer();
        actcont.action = action;
        actcont.delta = Instant.now().toEpochMilli();
        actions.add(actcont);
    }
}
