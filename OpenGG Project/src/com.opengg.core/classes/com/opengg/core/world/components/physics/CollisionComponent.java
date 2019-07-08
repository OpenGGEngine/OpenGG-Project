/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.physics;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.Collider;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;

import java.io.IOException;
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

    public CollisionComponent(ColliderGroup group){
        this();
        this.collidergroup = group;
    }

    public void addCollider(Collider collider) {
        this.collidergroup.addCollider(collider);
    }
    
    public ColliderGroup getColliderGroup(){
        return collidergroup;
    }
    
    @Override
    public void onWorldChange(){
        this.getWorld().getSystem().addObject(collidergroup);
    }
    
    @Override
    public void onPositionChange(Vector3f npos){
        collidergroup.setPosition(npos);
    }
    
    @Override
    public void onRotationChange(Quaternionf nrot){
        collidergroup.setRotation(nrot);
    }
    
    @Override
    public void onScaleChange(Vector3f nscale){
        collidergroup.setScale(nscale);
    }
    
    @Override
    public void update(float delta){
        
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(collidergroup.id);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        int id = in.readInt();

        OpenGG.asyncExec(() -> {
            this.collidergroup = (ColliderGroup) this.getWorld().getSystem().getObjectByID(id);
            collidergroup.setPosition(getPosition());
            collidergroup.setRotation(getRotation());
            collidergroup.setScale(getScale());
        });
    }

    @Override
    public void finalizeComponent(){
        this.getWorld().getSystem().removeObject(collidergroup);
    }
}
