/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsEntity;
import com.opengg.core.physics.PhysicsObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ColliderGroup extends PhysicsObject{
    AABB main;
    PhysicsEntity parent;
    List<Collider> colliders = new ArrayList<>();
    boolean lastcollided = false;
    boolean forcetest = false;
    
    public ColliderGroup(){
        this(new AABB(new Vector3f(0,0,0),1,1,1), new ArrayList<>());
    }
    
    public ColliderGroup(AABB main, List<Collider> all) {
        setBoundingBox(main);
        addColliders(all);
    }
    
    public ColliderGroup(AABB main, Collider... all) {
        this(main, Arrays.asList(all));
    }
    
    public void addCollider(Collider bb) {
        colliders.add(bb);
        bb.setParent(this);
    }
    
    public void addColliders(List<Collider> bb) {
        for(Collider c : bb){
            addCollider(c);
        }
    }
    
    public List<Collider> getColliders(){
        return colliders;
    }
    
    public void setBoundingBox(AABB box){
        this.main = box;
    }
    
    public void setParent(PhysicsEntity parent){
        this.parent = parent;
    }
    
    public Collision testForCollision(ColliderGroup other) {
        if (!main.isColliding(other.main) && !(this.forcetest || other.forcetest))
            return null;

        Collision c = null;
        for (Collider x: this.colliders) {
            for(Collider y: other.colliders) {
                x.updatePositions();
                y.updatePositions();
                ContactManifold data = x.isColliding(y);
                if ((data) != null){
                    if(c == null){
                        c = new Collision();
                        c.thiscollider = this;
                        c.other = other;
                    }
                    c.manifolds.add(data);
                }
            }
        }

        return c;
    }
    
    @Override
    public Vector3f getPosition(){
        if(parent != null)
            return parent.getPosition().add(parent.getRotation().transform(position));
        else
            return super.getPosition();
    }
    
    @Override
    public Quaternionf getRotation(){
        if(parent != null)
            return parent.getRotation().multiply(rotation);
        else
            return super.getRotation();
    }
}
