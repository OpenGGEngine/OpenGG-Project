/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.triggers.TriggerInfo;
import com.opengg.core.world.components.triggers.Triggerable;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class TriggerComponent extends Component implements com.opengg.core.world.components.triggers.Trigger {
    private ArrayList<Triggerable> subscribers = new ArrayList<>();


    public void trigger(TriggerInfo ti){
        onTrigger(ti);
        for(Triggerable t : subscribers){
            t.onTrigger(this, ti);
        }
    }

    public void onTrigger(TriggerInfo data){

    }

    public void addListener(Triggerable dest){
        subscribers.add(dest);
    }

    public ArrayList<Triggerable> getSubscribers() {
        return subscribers;
    }

    public void clearSubscribers() {
        subscribers = new ArrayList<>();
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(subscribers.size());
        for(Triggerable t : subscribers){
            Component c = (Component)t;
            out.write(c.getGUID());
        }
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        int size = in.readInt();
        long[] ids = new long[size];
        for(int i = 0; i < size; i++){
            ids[i] = in.readLong();
        }
        
        OpenGG.asyncExec(() ->{
            for(long i : ids){
                subscribers.add((Triggerable) WorldEngine.findEverywhereByGUID(i).get());
            }
        });
    }
}
