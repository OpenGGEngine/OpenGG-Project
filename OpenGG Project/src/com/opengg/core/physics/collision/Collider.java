/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.world.components.physics.CollisionComponent;
import com.opengg.core.math.Vector3f;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public abstract class Collider {
    CollisionComponent parent;
    Vector3f offset = new Vector3f();
    boolean serializable = true;
    public abstract Collision isColliding(Collider c);
    
    public Vector3f getPosition(){
        return offset.add(parent.getPosition());
    }
    
    public void setParent(CollisionComponent parent){
        this.parent = parent;
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
