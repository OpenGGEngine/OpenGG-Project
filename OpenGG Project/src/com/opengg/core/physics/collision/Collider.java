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
    ColliderGroup parent;
    
    public abstract ContactManifold isColliding(Collider c);
    
    @Override
    public Vector3f getPosition(){
        return parent.getPosition().add(parent.getRotation().transform(position));
    }
    
    @Override
    public Quaternionf getRotation(){
        return parent.getRotation().add(rotation);
    }
    
    public void serialize(GGOutputStream stream) throws IOException{
        stream.write(position);
        stream.write(rotation);
    }
    
    public void deserialize(GGInputStream stream) throws IOException{
        position = stream.readVector3f();
        rotation = stream.readQuaternionf();
    }
    
    public void setParent(ColliderGroup parent){
        this.parent = parent;
    }
}
