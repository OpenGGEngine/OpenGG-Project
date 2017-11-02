/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsObject;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public abstract class Collider extends PhysicsObject{
    Vector3f position = new Vector3f(), offset = new Vector3f();
    Vector3f scale = new Vector3f(), scaleoffset = new Vector3f();
    Quaternionf rot = new Quaternionf(), rotoffset = new Quaternionf();
    
    boolean serializable = true;
    public abstract Collision isColliding(Collider c);
    
    public Vector3f getPosition(){
        return offset.add(position);
    }
    
    public void setPosition(Vector3f pos){
        this.position = pos;
    }
    
    public void serialize(GGOutputStream stream) throws IOException{
        stream.write(offset);
    }
    
    public void deserialize(GGInputStream stream) throws IOException{
        offset = stream.readVector3f();
    }
    
    public boolean isSerializable(){
        return serializable;
    }
    
    public void setSerializable(boolean serializable){
        this.serializable = serializable;
    }
}
