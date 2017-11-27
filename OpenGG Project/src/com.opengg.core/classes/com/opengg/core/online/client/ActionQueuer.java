/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online.client;

import com.opengg.core.engine.BindController;
import com.opengg.core.util.Time;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionTransmitter;
import com.opengg.core.world.Serializer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ActionQueuer implements ActionTransmitter{
    static long lastTime;
    static Time t;
    static Client client;
    static ActionQueuer queuer;
    static List<ActionContainer> actions = new ArrayList<>();
    
    private ActionQueuer(){
        BindController.addController(this);
    }
    
    public static void initialize(Client client){
        queuer = new ActionQueuer();
        ActionQueuer.client = client;
        t = new Time();
        lastTime = Calendar.getInstance().getTimeInMillis();
    }
    /*
    public static byte[] generatePacket(){
        Serializer s = new Serializer();
        t.getDeltaMs();
        s.add(lastTime);
        s.add(actions.size());
        for(ActionContainer action : actions){
            s.add(action.action.name);
            s.add(action.action.type.name());
        }
        actions.clear();
        lastTime = Calendar.getInstance().getTimeInMillis();
        return s.getByteArray();
    }
    */
    @Override
    public void doAction(Action action) {
        
        ActionContainer actcont = new ActionContainer();
        actcont.action = action;
        actcont.delta = t.getDeltaMs();
        actions.add(actcont);
    }
}
