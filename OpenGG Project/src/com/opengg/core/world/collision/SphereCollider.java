/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.collision;

import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class SphereCollider extends Collider{
    float radius;
    
    public SphereCollider(){
        this(1);
    }
    
    public SphereCollider(float radius){
        this.radius = radius;
    }
    
    @Override
    public Collision isColliding(Collider c) {
        if(c instanceof SphereCollider)
            return CollisionUtil.SphereSphere(this, (SphereCollider)c);
        else if(c instanceof TerrainCollider)
            return CollisionUtil.SphereTerrain(this, (TerrainCollider)c);
        return null;
    }
    
    @Override
    public void serialize(GGOutputStream stream) throws IOException{
        super.serialize(stream);
        stream.write(radius);
    }
    
    @Override
    public void deserialize(GGInputStream stream) throws IOException{
        super.deserialize(stream);
        radius = stream.readFloat();
    }
}
