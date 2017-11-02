/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
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
    List<Collider> colliders = new ArrayList<>();
    boolean lastcollided = false;
    boolean forcetest = false;
    
    public ColliderGroup(){
        this(new AABB(new Vector3f(0,0,0),1,1,1), new ArrayList<>());
    }
    
    public ColliderGroup(AABB main, List<Collider> all) {
        setBoundingBox(main);
        colliders.addAll(all);
    }
    
    public ColliderGroup(AABB main, Collider... all) {
        this(main, Arrays.asList(all));
    }
    
    public void addCollider(Collider bb) {
        colliders.add(bb);
    }
    
    public void addColliders(List<Collider> bb) {
        colliders.addAll(bb);
    }
    
    public void setBoundingBox(AABB box){
        this.main = box;
    }
    
    public List<Collision> testForCollision(ColliderGroup other) {
        List<Collision> dataList = new ArrayList<>();
        
        if (!main.isColliding(other.main) && !(this.forcetest || other.forcetest))
            return dataList;


        for (Collider x: this.colliders) {
            for(Collider y: other.colliders) {
                Collision data = x.isColliding(y);
                if ((data) != null){
                    data.thiscollider = this;
                    data.other = other;
                    dataList.add(data);
                }
            }
        }

        return dataList;
    }
}
