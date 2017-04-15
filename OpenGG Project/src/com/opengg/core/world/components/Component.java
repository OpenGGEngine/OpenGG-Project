/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;
import com.opengg.core.world.World;
import java.io.Serializable;

/**
 * Component
 * @author Warren
 */
public abstract class Component implements Serializable{
    public static int curid = 0;
    public int id;
    public boolean absoluteOffset = false;
    public String name = "";
    public ComponentHolder parent;
    public Vector3f pos = new Vector3f();
    public Quaternionf rot = new Quaternionf();
    public Vector3f scale = new Vector3f(1,1,1);
    
    public Component(){
        id = curid;
        curid++;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setParentInfo(ComponentHolder parent){
        this.parent = parent;
    }
    
    public void setPositionOffset(Vector3f pos){
        this.pos = pos;
    }
    
    public void setRotationOffset(Quaternionf rot){
        this.rot = rot;
    }
    
    public void setScale(Vector3f scale){
        this.scale = scale;
    }
    
    public void setAbsoluteOffset(boolean abs){
        absoluteOffset = abs;
    }
    
    public boolean isAbsoluteOffset(){
        return absoluteOffset;
    }
    
    public Vector3f getPosition(){
        if(absoluteOffset){
            return parent.getPosition().add(pos);
        }else{
            return parent.getPosition().add(parent.getRotation().transform(pos));
        }
    }
    
    public Vector3f getPositionOffset(){
        return pos;
    }
    
    public Quaternionf getRotation(){
        return parent.getRotation().multiply(rot);
    }
        
    public Quaternionf getRotationOffset(){
        return rot;
    }
    
    public Vector3f getScale(){
        return new Vector3f(scale).multiply(parent.getScale());
    }
    
    public void update(float delta){
        
    }
    
    public void serialize(Serializer s){
        s.add(pos);
        s.add(rot);
        s.add(scale);
    }

    public void deserialize(Deserializer d){
        pos = d.getVector3f();
        rot = d.getQuaternionf();
        scale = d.getVector3f();
    }
    
    public World getWorld(){
        return parent.getWorld();
    }
    
    public void remove(){
        
    }
}
