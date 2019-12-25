/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.physics;

import com.opengg.core.GGInfo;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;

import java.io.IOException;

/**
 *
 * @author Javier
 */
public class RigidBodyComponent extends Component{
    RigidBody entity;
    private long id;

    public RigidBodyComponent(){
        this(new RigidBody(), false);
    }

    public RigidBodyComponent(RigidBody body, boolean physics){
        entity = body;
        entity.bindToComponent(this);
        id = entity.getId();
        if(physics)
            entity.enablePhysicsProvider();
    }

    public RigidBody getRigidBody(){
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
        this.getWorld().getSystem().addObject(entity);
    }

    @Override
    public void update(float delta) {
        this.getParent().setPositionOffset(entity.getPosition().subtract(this.getPositionOffset()));
        this.getParent().setRotationOffset(entity.getRotation());
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        entity.serialize(out);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        entity = new RigidBody();
        entity.deserialize(in);
        /*
        id = in.readLong();
        OpenGG.asyncExec(() -> this.entity = (RigidBody) this.getWorld().getSystem().getObjectByID(id));*/
    }

    @Override
    public void serializeUpdate(GGOutputStream out) throws IOException{
        getRigidBody().serializeUpdate(out);
    }

    @Override
    public void deserializeUpdate(GGInputStream in, float delta) throws IOException{
        getRigidBody().deserializeUpdate(in, delta);
    }

    @Override
    public void finalizeComponent(){
        if(this.getWorld() != null)
            this.getWorld().getSystem().removeObject(entity);
    }
}
