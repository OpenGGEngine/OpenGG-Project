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
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Deserializer {
    public static Deserializer ds;
    public GGInputStream b;
    public List<SerialHolder> components = new LinkedList<>();
    public World w;
    
    public static World deserialize(ByteBuffer b){
        ds = new Deserializer();
        ds.b = new GGInputStream(b);
        try {
            ds.w = new World();
            ds.w.deserialize(ds.b);
            ds.w.setId(0);
            doList(ds);
        } catch (IOException ex) {
            GGConsole.error("IOException thrown during deserialization of world!");
        }
        
        if(ds.w == null)
            return null;
        
        int maxid = 0;
        upper : for(SerialHolder sh : ds.components){
            if(sh.c.getId() > maxid){
                maxid = sh.c.getId();
            }
            
            if(sh.parent == 0){
                ds.w.attach(sh.c);
                continue;
            }
            
            for(SerialHolder sh2 : ds.components){
                if(sh2.c.getId() == sh.parent){
                    if(sh2.c instanceof Component){
                        ((Component)sh2.c).attach(sh.c);
                        continue upper;
                    }else{
                        GGConsole.warning("Component " + sh.c.getId() + " has invalid parent, will not be added");
                    }   
                }
            }
            GGConsole.warning("Component " + sh.c.getId()+ " has invalid parent, will not be added");
        }
        return ds.w;
    }
    
    public static void doList(Deserializer ds) throws IOException{
        int l = ds.b.readInt();
        for(int i = 0; i < l; i++){
            String classname = ds.b.readString();
            try {
                int id = ds.b.readInt();
                int pid = ds.b.readInt();
                Class c = Class.forName(classname);
                Component comp = (Component)c.getConstructor().newInstance();
                comp.deserialize(ds.b);
                comp.setId(id);
                
                SerialHolder ch = new SerialHolder();
                ch.c = comp;
                ch.parent = pid;
                ch.type = c;
                ds.components.add(ch);
                
                doList(ds);
            } catch (ClassNotFoundException ex) {
                GGConsole.error("Failed to load world, class " + classname + " is missing!");
                ds.w = null;
                return;
            } catch (SecurityException  ex) {
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
