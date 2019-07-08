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
    private int id;

    public PhysicsComponent(){
        entity = new PhysicsEntity();
        id = entity.id;
        this.onWorldChange(() -> this.getWorld().getSystem().addObject(entity));
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
       system.addObject(entity);
    }
    
    public PhysicsEntity getEntity(){
        return entity;
    }
    
    @Override
    public void onPositionChange(Vector3f npos){
        entity.setPosition(npos);
    }
    
    @Override
    public void onRotationChange(Quaternionf nrot){
        entity.setRotation(nrot);
    }

    @Override
    public void onWorldChange(){
    }

    @Override
    public void update(float delta) {
        this.getParent().setPositionOffset(entity.getPosition().subtract(this.getPositionOffset()));
        this.getParent().setRotationOffset(entity.getRotation());
    }
    
    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(entity.id);
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        id = in.readInt();
        this.onWorldChange(() -> this.entity = (PhysicsEntity) this.getWorld().getSystem().getObjectByID(id));

    }
    
    @Override
    public void finalizeComponent(){
        try{
            this.getWorld().getSystem().removeObject(entity);
        }catch (NullPointerException e){}
    }
}
