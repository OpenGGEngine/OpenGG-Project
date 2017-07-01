/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.util.GGByteInputStream;
import com.opengg.core.util.GGByteOutputStream;
import com.opengg.core.world.components.Component;
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
            GGByteOutputStream out = new GGByteOutputStream();
            out.write(Calendar.getInstance().getTimeInMillis());
            out.write(components.size());
            for(Component c : components){
                out.write(c.id);
                c.serialize(out);
            }
            
            return out.getArray();
        } catch (IOException ex) {
            GGConsole.error("Error occured during serialization of packet!");
        }
        return null;
    }
    
    public static void deserializeUpdate(byte[] bytes){
        try {
            GGByteInputStream ds = new GGByteInputStream(ByteBuffer.wrap(bytes));
            //long time = ds.readLong();
            int amount = ds.readInt();
            List<Component> components = WorldEngine.getCurrent().getAll();
            
            for(int i = 0; i < amount; i++){
                int id = ds.readInt();
                components.stream().filter((c) -> (c.id == id)).forEach((c) -> {
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
