/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsEntity;
import com.opengg.core.physics.PhysicsSystem;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import java.io.IOException;

/**
 *
 * @author ethachu19
 */
public class PhysicsComponent extends Component {
    PhysicsEntity entity;
    
    public PhysicsComponent(){
        entity = new PhysicsEntity();
    }
    
    public PhysicsComponent(ColliderGroup collider){
        this();
        entity.addCollider(collider);
    }

    public void addCollider(ColliderGroup c){
        entity.addCollider(c);
    }
    
    public void addCollider(CollisionComponent c){
        this.attach(c);
        entity.addCollider(c.getColliderGroup());
    }
    
    public void setSystem(PhysicsSystem system){
        entity.setSystem(system);
    }
    
    public PhysicsEntity getEntity(){
        return entity;
    }
    
    @Override
    public void onWorldChange(){
        entity.setSystem(this.getWorld().getSystem());
    }
    
    @Override
    public void onPositionChange(Vector3f npos){
        //entity.setPosition(npos);
    }
    
    @Override
    public void onRotationChange(Quaternionf nrot){
        entity.setRotation(nrot);
    }
    
    @Override
    public void update(float delta) {
        this.getParent().setPositionOffset(entity.position);
        this.getParent().setRotationOffset(entity.rotation);
    }
    
    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(entity.mass);
        out.write(entity.density);
        out.write(entity.dynamicfriction);
        out.write(entity.restitution);
        out.write(entity.velocity);
        out.write(entity.angvelocity);
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        entity = new PhysicsEntity(this.getWorld().getSystem());
        entity.mass = in.readFloat();
        entity.density = in.readFloat();
        entity.dynamicfriction = in.readFloat();
        entity.restitution = in.readFloat();
        entity.velocity = in.readVector3f();
        entity.angvelocity = in.readVector3f();
    }
    
    @Override
    public void finalizeComponent(){
        try{
            this.getWorld().getSystem().removeEntity(entity);
        }catch (NullPointerException e){}
    }
}