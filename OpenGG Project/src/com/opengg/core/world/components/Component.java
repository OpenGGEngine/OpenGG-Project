/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;

/**
 * Component
 * @author Warren
 */
public class Component {
    public Component parent;
    public Vector3f pos = new Vector3f();
    public Quaternionf rot = new Quaternionf();
   public  Vector3f scale = new Vector3f(1,1,1);
    
    public void setParentInfo(Component parent){
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
    
    public Vector3f getPosition(){
        return pos.add(parent.getPosition());
    }
    
    public Vector3f getPositionOffset(){
        return pos;
    }
    
    public Quaternionf getRotation(){
        return rot.add(parent.getRotation());
    }
    
    public Quaternionf getRotationOffset(){
        return rot;
    }
    
    public Vector3f getScale(){
        return scale.add(parent.getScale());
    }
    
    public void update(float delta){
        
    }
    
}
