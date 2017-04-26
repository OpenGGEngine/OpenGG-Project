/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.collision;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class SphereCollider extends Collider{
    double radius;
    
    public SphereCollider(double radius){
        this(radius, new Vector3f());
    }
    
    public SphereCollider(double radius, Vector3f offset){
        this.radius = radius;
        this.offset = offset;
    }
    
    @Override
    public Collision isColliding(Collider c) {
        if(c instanceof SphereCollider)
            return CollisionUtil.SphereSphere(this, (SphereCollider)c);
        else if(c instanceof TerrainCollider)
            return CollisionUtil.SphereTerrain(this, (TerrainCollider)c);
        return null;
    }
    
}
