/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.console.GGConsole;
import com.opengg.core.exceptions.ClassInstantiationException;
import com.opengg.core.util.ClassUtil;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.world.components.Component;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Deserializer {
    private GGInputStream in;
    private List<SerialHolder> components;
    public static List<ClassLoader> loaders = new ArrayList<>();
    private HashMap<Integer, String> classnames;
    private World world;
    
    public static World deserialize(ByteBuffer buffer) {
        var deserializer = new Deserializer(buffer);
        return deserializer.deserialize();
    }

    private Deserializer(ByteBuffer buffer){
        in = new GGInputStream(buffer);
        components = new LinkedList<>();
        classnames = new HashMap<>();
    }

    private World deserialize(){
        try {
            int namecount = in.readInt();
            for(int i = 0; i < namecount; i++){
                var id = in.readInt();
                var name = in.readString();
                classnames.put(id, name);
            }

            world = new World();
            world.deserialize(in);
            world.setId(0);
            doList();
        } catch (IOException ex) {
            GGConsole.error("IOException thrown during deserialization of world!");
        }
        
        if(world == null)
            return null;

        for(var holder : components){
            components
                    .stream()
                    .filter(holder2 -> holder.parent == holder2.comp.getId())
                    .map(holder2 -> holder2.comp)
                    .findFirst()
                    .ifPresentOrElse(comp -> comp.attach(holder.comp), () -> world.attach(holder.comp));
        }

        components.forEach(c -> c.comp.onWorldLoad());

        var highest = world.getAllDescendants()
                .stream()
                .mapToInt(Component::getId)
                .max().orElse(0);

        if(Component.getCurrentIdCounter() < highest) Component.setCurrentIdCounter(highest + 1);

        return world;
    }
    
    private void doList() throws IOException{
        int l = in.readInt();
        for(int i = 0; i < l; i++){
            String classname = classnames.get(in.readInt());
            try {
                int id = in.readInt();
                int pid = in.readInt();
                Object nclazz = ClassUtil.createByName(classname);

                Component comp = (Component)nclazz;
                comp.removeAll();

                int len = in.readInt();

                byte[] data = in.readByteArray(len);
                comp.deserialize(new GGInputStream(data));
                comp.setId(id);
                
                var holder = new SerialHolder();
                holder.comp = comp;
                holder.parent = pid;
                holder.type = comp.getClass();
                components.add(holder);

                doList();
            }  catch (ClassInstantiationException ex) {
                GGConsole.error("Failed to instantiate class " + classname + ": " + ex.getMessage());
            }
        }
    }

    /**
     *
     * @author Javier
     */
    public static class SerialHolder {
        Component comp;
        int parent;
        Class type;
    }
}
