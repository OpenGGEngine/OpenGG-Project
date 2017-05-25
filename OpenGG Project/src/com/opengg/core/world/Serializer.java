/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.Component;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Serializer {
    static Serializer serializer;
    List<Byte> bytes = new LinkedList<>();
    
    public static byte[] serialize(World w){
        serializer = new Serializer();
        
        w.serialize(serializer);
        traverse(w.getChildren());
        
        return serializer.getByteArray();
    }
    
    private static void traverse(List<Component> components){
        serializer.add(components.size());
        for(Component component : components){
            serializer.add(component.getClass().getName());
            serializer.add(component.id);
            serializer.add(component.parent.id);
            component.serialize(serializer);
            if(component instanceof Component){
                traverse(((Component) component).getChildren());
            }
        }
    }
    
    public byte[] getByteArray(){
        byte[] nbytes = new byte[bytes.size()];
        for(int i = 0; i < bytes.size(); i++){
            nbytes[i] = bytes.get(i);
        }
        return nbytes;
    }
    
    public void add(Vector2f v){
        add(v.x);
        add(v.y);
    }
    
    public void add(Vector3f v){
        add(v.x);
        add(v.y);
        add(v.z);
    }
    
    public void add(Quaternionf q){       
        addNormalized(q);
    }
    
    public void add(long l){
        add(ByteBuffer.allocate(Long.BYTES).putLong(l).array());
    }
    
    public void add(int i){
        add(ByteBuffer.allocate(Integer.BYTES).putInt(i).array());
    }
    
    public void add(float f){
        add(ByteBuffer.allocate(Float.BYTES).putFloat(f).array());
    }
    
    public void add(boolean b){
        add(b ? 1 : 0);
    }
    
    public void add(byte b){
        bytes.add(b);
    }
    
    public void add(byte[] b){
        for(byte by : b){
            add(by);
        }
    }
    
    public void add(char c){
        add(ByteBuffer.allocate(Character.BYTES).putChar(c).array());
    }
    
    public void add(String s){
        add(s.length());
        for(char c : s.toCharArray()){
            add(c);
        }
    }
    
    public void addNormalized(Vector2f v){
        add(v.x);
        add(v.y);
    }
    
    public void addNormalized(Vector3f v){
        add(v.x);
        add(v.y);
        add(v.z);
    }
    
    public void addNormalized(Quaternionf q){
        add(q.x);
        add(q.y);
        add(q.z);
        add(q.w);
    }
    
    
}
