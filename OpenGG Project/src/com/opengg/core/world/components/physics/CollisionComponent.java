/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.physics;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.math.Vector3f;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.Collider;
import com.opengg.core.physics.collision.Collision;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import static com.opengg.core.world.components.triggers.TriggerInfo.SINGLE;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ethachu19
 */
public class CollisionComponent extends Trigger{
    AABB main;
    List<Collider> colliders = new ArrayList<>();
    boolean lastcollided = false;
    boolean forcetest = false;

    public CollisionComponent(){
        this(new AABB(new Vector3f(0,0,0),1,1,1), new ArrayList<>());
    }
    
    public CollisionComponent(AABB main, List<Collider> all) {
        this.main = main;
        colliders.addAll(all);
        setColliderParent();
    }
    
    public CollisionComponent(AABB main, Collider... all) {
        this(main, Arrays.asList(all));
    }
    
    public void setForceTest(boolean test){
        forcetest = test;
    }
    
    private void setColliderParent(){
        for(Collider c : colliders){
            c.setParent(this);
        }
    }
    
    public void addCollider(Collider bb) {
        colliders.add(bb);
    }

    public List<Collision> testForCollision(CollisionComponent other) {
        List<Collision> dataList = new ArrayList<>();
        
        if (!main.isColliding(other.main) && !(this.forcetest || other.forcetest))
            return dataList;

        boolean collided = false;
        for (Collider x: this.colliders) {
            for(Collider y: other.colliders) {
                Collision data = x.isColliding(y);
                if ((data) != null){
                    collided = true;
                    data.thiscollider = this;
                    data.other = other;
                    dataList.add(data);
                }
            }
        }
        
        this.collisionTrigger(dataList);
        other.collisionTrigger(dataList);
        
        return dataList;
    }

    public PhysicsComponent getPhysicsComponent(){
        if(getParent() instanceof PhysicsComponent){
            return (PhysicsComponent)getParent();
        }
        return null;
    }
    
    @Override
    public void update(float delta) {
        Vector3f fpos = getPosition();
        main.recenter(fpos);
    }
    
    public void collisionTrigger(List<Collision> data){
        TriggerInfo ti = new TriggerInfo();
        ti.info = "collision";
        ti.type = SINGLE;
        ti.source = this;
        ti.data = data;
        trigger(ti);
        lastcollided = true;
    }
    
    @Override
    public void serialize(GGOutputStream stream) throws IOException{
        super.serialize(stream);
        stream.write(main.getPos());
        stream.write(main.getLWH());
        stream.write(getAllSerializable());
        for(Collider collider : colliders){
            if(collider.isSerializable()){
                stream.write(collider.getClass().getName());
                collider.serialize(stream);
            }
        }
    }
    
    @Override
    public void deserialize(GGInputStream stream) throws IOException{
        super.deserialize(stream);
        Vector3f mpos = stream.readVector3f();
        Vector3f lwh = stream.readVector3f();
        main = new AABB(mpos, lwh.x, lwh.y, lwh.z);
        int size = stream.readInt();

        for(int i = 0; i < size; i++){
            try {
                String clazzname = stream.readString();
                Class clazz = Class.forName(clazzname);
                Collider collider = (Collider)clazz.getConstructor().newInstance();
                collider.deserialize(stream);
                colliders.add(collider);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                GGConsole.error("Failed to load world, unable to instantiate colliders");
                ex.printStackTrace();
            }
        }
        setColliderParent();
    }
    
    private int getAllSerializable(){
        int i = 0;
        for(Collider c : colliders)
            if(c.isSerializable())
                i++;
        return i;
    }
}
