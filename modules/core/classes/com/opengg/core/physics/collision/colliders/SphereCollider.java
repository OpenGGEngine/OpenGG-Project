/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision.colliders;

import com.opengg.core.physics.collision.Collider;
import com.opengg.core.physics.collision.CollisionSolver;
import com.opengg.core.physics.collision.ContactManifold;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;
import java.util.Optional;

/**
 *
 * @author Javier
 */
public class SphereCollider extends Collider {
    float radius;
  
    public SphereCollider(){
        this(1);
    }
    
    public SphereCollider(float radius){
        this.radius = radius;
    }
    
    public float getRadius() {
        return radius;
    }
    
    @Override
    public Optional<ContactManifold> collide(Collider c) {
        if(c instanceof SphereCollider sh)
            return CollisionSolver.SphereSphere(this, sh);
        if(c instanceof CapsuleCollider cc)
            return CollisionSolver.SphereCapsule(this, cc);
        if(c instanceof ConvexHull ch)
            return CollisionSolver.HullSphere(ch, this).map(ContactManifold::reverse);
        return Optional.empty();
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
