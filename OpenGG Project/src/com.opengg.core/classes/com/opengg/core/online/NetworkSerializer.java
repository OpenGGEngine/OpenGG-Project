/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Javier
 */
public class NetworkSerializer {
    public static byte[] serializeUpdate(List<Component> components){
        try {
            GGOutputStream out = new GGOutputStream();
            out.write(Calendar.getInstance().getTimeInMillis());
            out.write(components.size());
            for(Component c : components){
                out.write(c.getId());
                c.serialize(out);
            }
            
            return ((ByteArrayOutputStream)out.getStream()).toByteArray();
        } catch (IOException ex) {
            GGConsole.error("Error occured during serialization of packet!");
        }
        return null;
    }
    
    public static void deserializeUpdate(byte[] bytes){
        try {
            GGInputStream ds = new GGInputStream(ByteBuffer.wrap(bytes));
            //long time = ds.readLong();
            int amount = ds.readInt();
            List<Component> components = WorldEngine.getCurrent().getAll();
            
            for(int i = 0; i < amount; i++){
                int id = ds.readInt();
                components.stream().filter((c) -> (c.getId() == id)).forEach((c) -> {
                    try {
                        c.deserialize(ds);
                    } catch (IOException ex) {
                        GGConsole.error("Error occured during deserialization of packet!");
                    }
                });
            }
        } catch (IOException ex) {
            GGConsole.error("Error occured during deserialization of packet!");
        }
    }
}
