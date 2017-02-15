/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ComponentHolder;

/**
 *
 * @author Javier
 */
public class World extends ComponentHolder{
    public float floorLev = 0;
    public Vector3f gravityVector = new Vector3f(0,-9.81f,0);
    public Vector3f wind = new Vector3f();

    public void setFloor(float floor){
        floorLev = floor;
    }
    
    @Override
    public Vector3f getPosition(){
        return new Vector3f();
    }
    
    @Override
    public Quaternionf getRotation(){
        return new Quaternionf();
    }

    @Override
    public Vector3f getScale(){
        return new Vector3f();
    }
    
    @Override
    public void setParentInfo(Component parent) {
        throw new InvalidParentException("World must be the top level component!");
    }
    
    @Override
    public void serialize(Serializer s){
        super.serialize(s);
        s.add(gravityVector);
        s.add(floorLev);
        s.add(wind);
        
    }
    
    @Override
    public void deserialize(Deserializer ds){
        super.deserialize(ds);
        gravityVector = ds.getVector3f();
        floorLev = ds.getFloat();
        wind = ds.getVector3f();
    }
}
