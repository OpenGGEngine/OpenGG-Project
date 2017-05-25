/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.Component;
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
    public ByteBuffer b;
    public List<SerialHolder> components = new LinkedList<>();
    public World w;
    
    public static World deserialize(ByteBuffer b){
        ds = new Deserializer();
        ds.b = b;
        
        ds.w = new World();
        ds.w.deserialize(ds);
        ds.w.id = 0;
        
        doList(ds);
        
        if(ds.w == null)
            return null;
        
        int maxid = 0;
        upper : for(SerialHolder sh : ds.components){
            if(sh.c.id > maxid){
                maxid = sh.c.id;
            }
            
            if(sh.parent == 0){
                ds.w.attach(sh.c);
                continue;
            }
            
            for(SerialHolder sh2 : ds.components){
                if(sh2.c.id == sh.parent){
                    if(sh2.c instanceof Component){
                        ((Component)sh2.c).attach(sh.c);
                        continue upper;
                    }else{
                        GGConsole.warning("Component " + sh.c.id + " has invalid parent, will not be added");
                    }   
                }
            }
            GGConsole.warning("Component " + sh.c.id + " has invalid parent, will not be added");
        }
        return ds.w;
    }
    
    public static void doList(Deserializer ds){
        int l = ds.getInt();
        for(int i = 0; i < l; i++){
            String classname = ds.getString();
            try {
                int id = ds.getInt();
                int pid = ds.getInt();
                Class c = Class.forName(classname);
                Component comp = (Component)c.getConstructor().newInstance();
                comp.deserialize(ds);
                comp.id = id;
                
                SerialHolder ch = new SerialHolder();
                ch.c = comp;
                ch.parent = pid;
                ch.type = c;
                ds.components.add(ch);
                
                if(comp instanceof Component){
                    doList(ds);
                }
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
    
    public Vector3f getVector3f(){
        Vector3f v = new Vector3f();
        v.x = getFloat();
        v.y = getFloat();
        v.z = getFloat();
        return v;
    }
    
    public Vector2f getVector2f(){
        Vector2f v = new Vector2f();
        v.x = getFloat();
        v.y = getFloat();
        return v;
    }
    
    public Quaternionf getQuaternionf(){
        Quaternionf f = new Quaternionf();
        f.x = getFloat();
        f.y = getFloat();
        f.z = getFloat();
        f.w = getFloat();
        return f;
    }
    
    public int getInt(){
        ByteBuffer b = ByteBuffer.allocate(Integer.BYTES).put(getByteArray(Integer.BYTES));
        b.flip();
        return b.getInt();
    }
    
    public float getFloat(){
        ByteBuffer b = ByteBuffer.allocate(Float.BYTES).put(getByteArray(Float.BYTES));
        b.flip();
        return b.getFloat();
    }
    
    public boolean getBoolean(){
        byte b = getByte();
        
        return b == 1;
    }
    
    public byte[] getByteArray(int size){
        byte[] b = new byte[size];
        for(int i = 0; i < size; i++){
            b[i] = getByte();
        }
        return b;
    }
    
    public byte getByte(){
        return b.get();
    }
    
    public char getChar(){
        ByteBuffer b = ByteBuffer.allocate(Character.BYTES).put(getByteArray(Character.BYTES));
        b.flip();
        return (char)b.getShort();
    }
    
    public String getString(){
        String s = "";
        int len = getInt();
        for(int i = 0; i < len; i++){
            s += getChar();
        }
        return s;
    }
    
    public Vector2f getNormalizedVector2f(){
        return getVector2f();
    }
    
    public Vector3f getNormalizedVector3f(){
        return getVector3f();
    }
    
    public Quaternionf getNormalizedQuaternionf(){
        return new Quaternionf();
    }
}
