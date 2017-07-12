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
            
            w.serialize(serializer.stream);
            traverse(w.getChildren());
            
            return serializer.stream.getArray();
        } catch (IOException ex) {
            GGConsole.error("IOException thrown during serialization of world!");
        }
        return null;
    }
    
    private static void traverse(List<Component> components) throws IOException{
        serializer.stream.write(components.size());
        for(Component component : components){
            serializer.stream.write(component.getClass().getName());
            serializer.stream.write(component.getId());
            serializer.stream.write(component.parent.getId());
            component.serialize(serializer.stream);
            if(component instanceof Component){
                traverse(((Component) component).getChildren());
            }
        }
    }
}
