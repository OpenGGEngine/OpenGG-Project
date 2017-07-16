/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.util.GGByteOutputStream;
import com.opengg.core.world.components.Component;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Serializer {
    static Serializer serializer;
    GGByteOutputStream stream;
    
    public static byte[] serialize(World w){
        try {

            serializer = new Serializer();
            serializer.stream = new GGByteOutputStream();
            
            w.serialize(serializer.stream);
            traverse(w.getChildren());          
            
            return serializer.stream.getArray();
        } catch (IOException ex) {
            GGConsole.error("IOException thrown during serialization of world!");
        }
        return null;
    }
    
    private static void traverse(List<Component> components) throws IOException{
        serializer.stream.write(getAllSerializable(components));
        for(Component component : components){
            if(component.shouldSerialize()){
                serializer.stream.write(component.getClass().getName());
                serializer.stream.write(component.getId());
                serializer.stream.write(component.getParent().getId());
                component.serialize(serializer.stream);
                traverse(component.getChildren());
            }
        }
    }
    
    private static int getAllSerializable(List<Component> components){
        int i = 0;
        for(Component c : components)
            if(c.shouldSerialize())
                i++;
        return i;
    }
}
