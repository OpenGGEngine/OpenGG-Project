/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online;

import com.opengg.core.engine.WorldEngine;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;
import com.opengg.core.world.components.Component;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Javier
 */
public class NetworkSerializer {
    public static byte[] serializeUpdate(List<Component> components){
        Serializer serializer = new Serializer();
        serializer.add(Calendar.getInstance().getTimeInMillis());
        serializer.add(components.size());
        for(Component c : components){
            serializer.add(c.id);
            c.serialize(serializer);
        }
        
        return serializer.getByteArray();
    }
    
    public static void deserializeUpdate(byte[] bytes){
        Deserializer ds = new Deserializer();
        ds.b = ByteBuffer.wrap(bytes);
        ds.b.rewind();
        long time = ds.b.getLong();
        int amount = ds.b.getInt();
        List<Component> components = WorldEngine.getCurrent().getAll();
                
        for(int i = 0; i < amount; i++){
            int id = ds.b.getInt();
            components.stream().filter((c) -> (c.id == id)).forEach((c) -> {
                c.deserialize(ds);
            });
        }
    }
}
