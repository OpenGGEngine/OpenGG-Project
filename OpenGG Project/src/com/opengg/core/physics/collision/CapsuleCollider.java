/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class CapsuleCollider extends Collider{
    float radius;
    Vector3f p1, p2;
    
    public CapsuleCollider(){
        this(new Vector3f(), new Vector3f(0,1,0),1);
    }
    
    public CapsuleCollider(Vector3f p1, float radius){
        this(new Vector3f(), p1, radius);
    }
    
    public CapsuleCollider(Vector3f p1, Vector3f p2, float radius){
        this.radius = radius;
        this.p1 = p1;
        this.p2 = p2;
    }
    
    public Vector3f getP1(){
        return p1.add(getPosition());
    }
    
    public Vector3f getP2(){
        return p2.add(getPosition());
    }
    
    @Override
    public ContactManifold isColliding(Collider c) {
        if(c instanceof SphereCollider)
            return CollisionSolver.SphereCylinder((SphereCollider)c, this);
        else if(c instanceof CapsuleCollider)
            return CollisionSolver.CapsuleCapsule((CapsuleCollider)c, this);
        else if(c instanceof TerrainCollider)
            return CollisionSolver.CylinderTerrain(this, (TerrainCollider)c);
        
        return null;
    }
    
    @Override
    public void serialize(GGOutputStream stream) throws IOException{
        super.serialize(stream);
        stream.write(radius);
        stream.write(p1);
        stream.write(p2);
        
    }
    
    @Override
    public void deserialize(GGInputStream stream) throws IOException{
        super.deserialize(stream);
        radius = stream.readFloat();
        p1 = stream.readVector3f();
        p1 = stream.readVector3f();
    }
}
