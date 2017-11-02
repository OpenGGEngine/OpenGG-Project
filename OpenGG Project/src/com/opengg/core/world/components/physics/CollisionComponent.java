/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.physics;

import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.Collider;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.world.components.Component;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Javier
 */
public class CollisionComponent extends Component{

    private ColliderGroup collidergroup = new ColliderGroup();
    
    public CollisionComponent(){
        
    }
    
    public CollisionComponent(AABB main, Collider... colliders){
        this();
        collidergroup.setBoundingBox(main);
        collidergroup.addColliders(Arrays.asList(colliders));
    }
    
    public CollisionComponent(AABB main, List<Collider> colliders){
        this();
        collidergroup.setBoundingBox(main);
        collidergroup.addColliders(colliders);
    }

    public void addCollider(Collider collider) {
        this.collidergroup.addCollider(collider);
    }
    
    public ColliderGroup getColliderGroup(){
        return collidergroup;
    }
    
    @Override
    public void onWorldChange(){
        this.getWorld().getSystem().addCollider(collidergroup);
    }
    
    @Override
    public void update(float delta){
        
    }
    
    @Override
    public void finalizeComponent(){
        this.getWorld().getSystem().removeCollider(collidergroup);
    }
}
