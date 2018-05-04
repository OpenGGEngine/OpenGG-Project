/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.console.GGConsole;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.world.components.Component;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Deserializer {
    public static Deserializer deserializer;
    public GGInputStream b;
    public List<SerialHolder> components = new LinkedList<>();
    public static List<ClassLoader> loaders = new ArrayList<>();
    public World w;
    
    public static World deserialize(ByteBuffer buffer){
        deserializer = new Deserializer();
        deserializer.b = new GGInputStream(buffer);
        try {
            deserializer.w = new World();
            deserializer.w.deserialize(deserializer.b);
            deserializer.w.setId(0);
            doList(deserializer);
        } catch (IOException ex) {
            GGConsole.error("IOException thrown during deserialization of world!");
        }
        
        if(deserializer.w == null)
            return null;
        
        int maxid = 0;
        upper : for(SerialHolder sh : deserializer.components){
            if(sh.c.getId() > maxid){
                maxid = sh.c.getId();
            }
            
            if(sh.parent == 0){
                deserializer.w.attach(sh.c);
                continue;
            }
            
            for(SerialHolder sh2 : deserializer.components){
                if(sh2.c.getId() == sh.parent){
                    if(sh2.c instanceof Component){
                        sh2.c.attach(sh.c);
                        continue upper;
                    }else{
                        GGConsole.warning("Component " + sh.c.getId() + " has invalid parent, will not be added");
                    }   
                }
            }
            GGConsole.warning("Component " + sh.c.getId()+ " has invalid parent, will not be added");
        }
        return deserializer.w;
    }
    
    public static void doList(Deserializer ds) throws IOException{
        int l = ds.b.readInt();
        for(int i = 0; i < l; i++){
            String classname = ds.b.readString();
            try {
                int id = ds.b.readInt();
                int pid = ds.b.readInt();
                Class clazz = null;
                try{
                    clazz = Class.forName(classname);
                }catch (ClassNotFoundException ex) {
                    for(ClassLoader cl : loaders){
                        try{
                            clazz = Class.forName(classname, true, cl);
                        }catch(ClassNotFoundException e){
                           throw new RuntimeException("Failed to create class " + classname + " during deserialization!");
                        }
                    }
                }

                Component comp = (Component)clazz
                        .getConstructor()
                        .newInstance();
                comp.deserialize(ds.b);
                comp.setId(id);
                
                SerialHolder ch = new SerialHolder();
                ch.c = comp;
                ch.parent = pid;
                ch.type = clazz;
                ds.components.add(ch);

                doList(ds);
            }  catch (SecurityException  ex) {
                GGConsole.error("Failed to load world, access to " + classname + " is not allowed");
                ds.w = null;
                return;
            } catch (InstantiationException | IllegalAccessException ex) {
                GGConsole.error("Failed to load world, could not create instance of " + classname);
                ds.w = null;
                return;
            } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
                GGConsole.error("Failed to load world, could not access default constructor for " + classname);
                ds.w = null;
                return;
            }
        }
    }
}
