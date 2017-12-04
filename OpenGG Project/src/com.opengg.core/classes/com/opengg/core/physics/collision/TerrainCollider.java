/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.world.Terrain;

/**
 *
 * @author Javier
 */
public class TerrainCollider extends Collider{
    Terrain t;

    public TerrainCollider(Terrain t){
        this.t = t;
    }
    
    @Override
    public Contact isColliding(Collider c) {
        if(c instanceof SphereCollider)
            return CollisionSolver.SphereTerrain((SphereCollider)c, this);
        if(c instanceof CapsuleCollider)
            return CollisionSolver.CylinderTerrain((CapsuleCollider)c, this);
        return null;
    }
}
