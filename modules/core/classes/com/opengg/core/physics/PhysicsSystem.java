/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics;

import com.opengg.core.exceptions.ClassInstantiationException;
import com.opengg.core.physics.collision.CollisionManager;
import com.opengg.core.physics.mechanics.ForceGenerator;
import com.opengg.core.physics.mechanics.GravityGenerator;
import com.opengg.core.util.ClassUtil;
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
public class PhysicsSystem {
    private final RigidBody floor = new RigidBody();

    private final PhysicsConstants constants = new PhysicsConstants();
    private final List<PhysicsObject> objects = new ArrayList<>();

    private long tick;

    public PhysicsSystem(){

        objects.add(new GravityGenerator());
    }

    public List<PhysicsObject> getObjects(){
        return objects;
    }

    public void addObject(PhysicsObject entity){
        if(objects.contains(entity)) return;
        objects.add(entity);
        entity.system = this;
        entity.onSystemChange();
    }

    public void removeObject(PhysicsObject entity){
        objects.remove(entity);
    }

    public long getTick() {
        return tick;
    }

    public PhysicsConstants getConstants(){
        return constants;
    }

    public void update(float delta) {
        CollisionManager.clearCollisions();

        var forces = objects.stream().filter(o -> o instanceof ForceGenerator).map(o -> (ForceGenerator)o).collect(Collectors.toList());
        var rigidBodies = objects.stream().filter(o -> o instanceof RigidBody).map(o -> (RigidBody)o).collect(Collectors.toList());

        for(var force : forces){
            for(var body : rigidBodies){
                body.getPhysicsProvider().ifPresent(force::applyTo);
            }
        }

        for(var body : rigidBodies){
            body.internalUpdate(delta);
        }
        CollisionManager.runCollisionStep(this);
        tick++;
    }

    public void serialize(GGOutputStream out) throws IOException {
        out.write(constants.BASE);
        out.write(constants.GRAVITY);

        out.write(objects.stream().filter(PhysicsObject::shouldSerialize).collect(Collectors.toList()).size());
        for(var entity : objects){
            if(!entity.shouldSerialize()) continue;
            out.write(entity.getClass().getName());
            entity.serialize(out);
        }
    }

    public void deserialize(GGInputStream in) throws IOException {
        constants.BASE = in.readFloat();
        constants.BASE = 1f;
        constants.GRAVITY = in.readVector3f();

        objects.clear();
        int count = in.readInt();

        for(int i = 0; i < count; i++){
            try {
                var entity = (PhysicsObject) ClassUtil.createByName(in.readString());
                entity.deserialize(in);
                addObject(entity);
            } catch (ClassInstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public PhysicsObject getObjectByID(long id){
        for (var entity : objects) {
            if(entity.id == id) return entity;
        }
        return null;
    }
}
