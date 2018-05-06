/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.console.GGConsole;
import com.opengg.core.exceptions.ClassInstantiationException;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.PhysicsEntity;
import com.opengg.core.physics.PhysicsObject;
import com.opengg.core.util.ClassUtil;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ColliderGroup extends PhysicsObject{
    public static int idcount;
    public int id;

    AABB aabb;
    PhysicsEntity parent;
    List<Collider> colliders = new ArrayList<>();
    boolean lastcollided = false;
    boolean forcetest = false;
    
    public ColliderGroup(){
        this(new AABB(1,1,1), new ArrayList<>());
    }

    public ColliderGroup(AABB main, Collider... all) {
        this(main, Arrays.asList(all));
    }

    public ColliderGroup(AABB main, List<Collider> all) {
        id = idcount;
        idcount++;
        setBoundingBox(main);
        addColliders(all);
    }
    
    public void addCollider(Collider bb) {
        colliders.add(bb);
        bb.setParent(this);
    }
    
    public void addColliders(List<Collider> bb) {
        for(Collider c : bb){
            addCollider(c);
        }
    }
    
    public List<Collider> getColliders(){
        return colliders;
    }
    
    public void setBoundingBox(AABB box){
        this.aabb = box;
        box.parent = this;
    }
    
    public void setParent(PhysicsEntity parent){
        this.parent = parent;
    }
    
    public Collision testForCollision(ColliderGroup other) {
        this.aabb.recalculate();
        other.aabb.recalculate();
        if (!aabb.isColliding(other.aabb) && !(this.forcetest || other.forcetest))
            return null;
        Collision c = null;
        for (Collider x: this.colliders) {
            for(Collider y: other.colliders) {
                x.updatePositions();
                y.updatePositions();
                Contact data = x.isColliding(y);
                if ((data) != null){
                    if(c == null){
                        c = new Collision();
                        c.thiscollider = this;
                        c.other = other;
                    }
                    c.manifolds.addAll(data.manifolds);
                }
            }
        }

        return c;
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        out.write(id);
        out.write(aabb.lwh);

        out.write(this.colliders.size());
        for(var collider : colliders){
            out.write(collider.getClass().getName());
            collider.serialize(out);
        }
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        id = in.readInt();
        var lwh = in.readVector3f();

        this.setBoundingBox(new AABB(lwh));

        var colsize = in.readInt();
        for(int i = 0; i < colsize; i++){
            var classname = in.readString();
            try {
                var collider = (Collider)ClassUtil.createByName(classname);
                collider.deserialize(in);
                addCollider(collider);
            } catch (ClassInstantiationException e) {
                GGConsole.error("Failed to insantiate collider with classname " + classname + ": " + e.getMessage());
            }
        }
    }

    @Override
    public Vector3f getPosition(){
        if(parent != null)
            return parent.getPosition().add(parent.getRotation().transform(position));
        else
            return super.getPosition();
    }
    
    @Override
    public Quaternionf getRotation(){
        if(parent != null)
            return parent.getRotation().multiply(rotation);
        else
            return super.getRotation();
    }
}
