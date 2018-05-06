/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.physics.PhysicsObject;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public abstract class Collider extends PhysicsObject{
    public abstract Contact isColliding(Collider c);

    public void updatePositions(){}

    @Override
    public void serialize(GGOutputStream stream) throws IOException{
        stream.write(position);
        stream.write(rotation);
    }

    @Override
    public void deserialize(GGInputStream stream) throws IOException{
        position = stream.readVector3f();
        rotation = stream.readQuaternionf();
    }
    
    public void setParent(PhysicsObject parent){
        this.parent = parent;
    }
}

