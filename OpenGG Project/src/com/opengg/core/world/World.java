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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class World extends ComponentHolder{
    private List<Component> objs = new ArrayList<>();
    public float floorLev = 0;
    public Vector3f gravityVector = new Vector3f(0,-9.81f,0);
    public Vector3f wind = new Vector3f();

    public void setFloor(float floor){
        floorLev = floor;
    }
    
    public List getObjects(){
        return objs;
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
}
