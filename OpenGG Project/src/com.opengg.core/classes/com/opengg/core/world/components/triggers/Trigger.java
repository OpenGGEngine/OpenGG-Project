/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.triggers;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class Trigger extends Component implements ITrigger{
    ArrayList<Triggerable> subscribers = new ArrayList<>();

    @Override
    public void addSubscriber(Triggerable dest){
        subscribers.add(dest);
        dest.onSubscribe(this);
    }
    
    @Override
    public void trigger(TriggerInfo ti){
        onTrigger(ti);
        for(Triggerable t : subscribers){
            t.onTrigger(this, ti);
        }
    }

    public void onTrigger(TriggerInfo data){

    }

    @Override
    public ArrayList<Triggerable> getSubscribers() {
        return subscribers;
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(subscribers.size());
        for(Triggerable t : subscribers){
            Component c = (Component)t;
            out.write(c.getId());
        }
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        int size = in.readInt();
        int[] ids = new int[size];
        for(int i = 0; i < size; i++){
            ids[i] = in.readInt();
        }
        
        OpenGG.asyncExec(() ->{
            for(int i : ids){
                subscribers.add((Triggerable) this.getWorld().find(i));
            }
        });
    }
}
