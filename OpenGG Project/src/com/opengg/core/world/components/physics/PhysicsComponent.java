/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.engine.PhysicsEngine;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsEntity;
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
        this(10);
    }
    
    public PhysicsComponent(float mass){
        entity = new PhysicsEntity(mass);
        addEntity();
    }
    
    public PhysicsComponent(CollisionComponent collider){
        entity = new PhysicsEntity();
        entity.addCollider(collider);
        addEntity();
    }
    
    private void addEntity(){
        PhysicsEngine.addEntity(entity);
    }

    @Override
    public void update(float delta) {
        Vector3f tpos = entity.position;
        Vector3f trot = entity.rotation;

        this.getParent().setPositionOffset(tpos);
        this.getParent().setRotationOffset(new Quaternionf(trot));
        
        entity.position = getPosition();
        entity.rotation = getRotation().toEuler();
    }
    
    public void addCollider(CollisionComponent c){
        entity.addCollider(c);
    }
    
    public PhysicsEntity getEntity(){
        return entity;
    }
    
    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(entity.mass);
        out.write(entity.density);
        out.write(entity.frictionCoefficient);
        out.write(entity.bounciness);
        out.write(entity.velocity);
        out.write(entity.angvelocity);
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        entity = new PhysicsEntity();
        entity.mass = in.readFloat();
        entity.density = in.readFloat();
        entity.frictionCoefficient = in.readFloat();
        entity.bounciness = in.readFloat();
        entity.velocity = in.readVector3f();
        entity.angvelocity = in.readVector3f();
    }
    
    @Override
    public void finalizeComponent(){
        PhysicsEngine.removeEntity(entity);
    }
}
