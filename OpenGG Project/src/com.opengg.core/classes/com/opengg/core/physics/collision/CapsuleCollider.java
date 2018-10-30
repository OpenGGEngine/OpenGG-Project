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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    
    public float getRadius(){
        return radius;
    }
    
    public Vector3f getP1(){
        return parent.getPosition().add(parent.getRotation().transform(getPosition().add(p1)));
    }
    
    public Vector3f getP2(){
        return parent.getPosition().add(parent.getRotation().transform(getPosition().add(p2)));
    }
    
    @Override
    public List<ContactManifold> collide(Collider c) {
        if(c instanceof SphereCollider){
            List<ContactManifold> cm = CollisionSolver.SphereCapsule((SphereCollider)c, this).stream().map(s -> s.reverse()).collect(Collectors.toList());

        }else if(c instanceof CapsuleCollider)
            return CollisionSolver.CapsuleCapsule(this, (CapsuleCollider)c);
        else if(c == null)
            return CollisionSolver.CapsuleGround(this);
        
        return new ArrayList<>();
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
