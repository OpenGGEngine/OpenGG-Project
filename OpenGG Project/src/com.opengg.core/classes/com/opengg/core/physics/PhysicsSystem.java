/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics;

import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.CollisionManager;
import com.opengg.core.physics.collision.ConvexHull;
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
    private final ColliderGroup floor = new ColliderGroup();

    private final PhysicsConstants constants = new PhysicsConstants();
    private List<PhysicsObject> objects = new ArrayList<>();

    public PhysicsSystem(){
        floor.setForceTest(true);
        floor.getColliders().add(new ConvexHull(List.of(
                new Vector3f(-10000,constants.BASE-20,-10000),
                new Vector3f(-10000,constants.BASE-20,10000),
                new Vector3f(-10000,constants.BASE,-10000),
                new Vector3f(-10000,constants.BASE,10000),
                new Vector3f(10000,constants.BASE-20,-10000),
                new Vector3f(10000,constants.BASE-20,10000),
                new Vector3f(10000,constants.BASE,-10000),
                new Vector3f(10000,constants.BASE,10000)
        )));
        //floor.setPosition(new Vector3f(0, constants.BASE ,0));
        objects.add(floor);
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

    public PhysicsConstants getConstants(){
        return constants;
    }
    
    public void update(float delta) {
        CollisionManager.clearCollisions();
        for(PhysicsObject entity : objects){
            entity.internalUpdate(delta);
        }
        CollisionManager.runCollisionStep(this);
    }

    public void serialize(GGOutputStream out) throws IOException {
        out.write(constants.BASE);
        out.write(constants.GRAVITY);

        out.write(objects.size());
        for(var entity : objects){
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
            PhysicsEntity entity = new PhysicsEntity();
            entity.deserialize(in);
            addObject(entity);
        }

        PhysicsEntity.idcount = objects.stream()
                .mapToInt(PhysicsObject::getId)
                .max().orElse(0);
    }

    public PhysicsObject getObjectByID(int id){
        for (var entity : objects) {
            if(entity.id == id) return entity;
        }
        return null;
    }
}
