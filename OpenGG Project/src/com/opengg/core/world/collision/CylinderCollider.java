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
public class CylinderCollider extends Collider{
    float radius, height;
    
    public CylinderCollider(){
        this(1,1);
    }
    
    public CylinderCollider(float radius, float height){
        this.radius = radius;
        this.height = height;
    }
    
    @Override
    public Collision isColliding(Collider c) {
        if(c instanceof SphereCollider)
            return CollisionUtil.SphereCylinder((SphereCollider)c, this);
        else if(c instanceof CylinderCollider)
            return CollisionUtil.CylinderCylinder((CylinderCollider)c, this);
        else if(c instanceof TerrainCollider)
            return CollisionUtil.CylinderTerrain(this, (TerrainCollider)c);
        
        return null;
    }
    
    @Override
    public void serialize(GGOutputStream stream) throws IOException{
        super.serialize(stream);
        stream.write(radius);
        stream.write(height);
        
    }
    
    @Override
    public void deserialize(GGInputStream stream) throws IOException{
        super.deserialize(stream);
        radius = stream.readFloat();
        height = stream.readFloat();
    }
}
