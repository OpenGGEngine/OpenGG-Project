/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.collision;

import com.opengg.core.world.Terrain;

/**
 *
 * @author Javier
 */
public class TerrainCollider extends Collider{
    Terrain t;

    public TerrainCollider(Terrain t){
        this.t = t;
        this.serializable = false;
    }
    
    @Override
    public Collision isColliding(Collider c) {
        if(c instanceof SphereCollider)
            return CollisionUtil.SphereTerrain((SphereCollider)c, this);
        if(c instanceof CylinderCollider)
            return CollisionUtil.CylinderTerrain((CylinderCollider)c, this);
        return null;
    }
}
