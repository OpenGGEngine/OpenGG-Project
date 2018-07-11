/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.console.GGConsole;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class Serializer {
    private GGOutputStream stream;
    private HashMap<String, Integer> classnames;
    private World world;

    public static byte[] serialize(World world) {
        try {

            var serializer = new Serializer(world);
            serializer.serialize();

            return ((ByteArrayOutputStream) serializer.stream.getStream()).toByteArray();
        } catch (IOException ex) {
            GGConsole.error("IOException thrown during serialization of world!");
        }
        return null;
    }

    private Serializer(World world) {
        this.classnames = new HashMap<>();
        this.world = world;
        this.stream = new GGOutputStream();
    }

    private void serialize() throws IOException {
        var allcomps = world.getAll();

        var allserializablenames = allcomps
                .stream()
                .filter(comp -> comp.shouldSerialize())
                .map(comp -> comp.getClass().getName())
                .distinct()
                .collect(Collectors.toList());

        for(int i = 0; i < allserializablenames.size(); i++){
            classnames.put(allserializablenames.get(i), i);
        }

        stream.write(classnames.size());

        for(var entry : classnames.entrySet()){
            stream.write(entry.getValue());
            stream.write(entry.getKey());
        }

        world.serialize(stream);
        traverse(world.getChildren());
    }
    
    private void traverse(List<Component> components) throws IOException{
        stream.write(getAllSerializable(components));
        for(Component component : components){
            if(component.shouldSerialize()){
                stream.write(classnames.get(component.getClass().getName()));
                stream.write(component.getId());
                stream.write(component.getParent().getId());

                var substream = new GGOutputStream();
                component.serialize(substream);

                stream.write(substream.asByteArray().length);
                stream.write(substream.asByteArray());
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
