/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.console.GGConsole;
import com.opengg.core.exceptions.ClassInstantiationException;
import com.opengg.core.math.UnorderedTuple;
import com.opengg.core.physics.PhysicsObject;
import com.opengg.core.util.ClassUtil;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.opengg.core.physics.collision.ContactManifold.averageContactManifolds;

/**
 *
 * @author Javier
 */
public class ColliderGroup extends PhysicsObject{
    public static int idcount;
    public int id;

    private List<Collider> colliders = new ArrayList<>();

    private AABB aabb;
    private boolean forceTest = false;
    
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
    
    public void addCollider(Collider collider) {
        children.add(collider);
        colliders.add(collider);
        collider.setParent(this);
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
        box.setParent(this);
        children.add(box);
    }

    public AABB getBoundingBox() {
        return aabb;
    }


    public void setForceTest(boolean forceTest) {
        this.forceTest = forceTest;
    }

    public Optional<Collision> testForCollision(ColliderGroup other) {
        this.aabb.recalculate();
        other.aabb.recalculate();
        if (!aabb.isColliding(other.aabb) && !(this.forceTest || other.forceTest))
            return Optional.ofNullable(null);
        this.getColliders().forEach(Collider::updatePositions);
        other.getColliders().forEach(Collider::updatePositions);

        var collisions = this.getColliders().stream()
                .flatMap(s -> other.getColliders().stream()
                            .map(s2 -> new UnorderedTuple<>(s,s2)))
                .distinct()
                .flatMap(t -> t.x.collide(t.y).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Optional.ofNullable(!collisions.isEmpty() ? new Collision(this, other, averageContactManifolds(collisions)) : null);
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        out.write(id);
        out.write(aabb.lwh);

        out.write(this.children.size()-1);
        for(var collider : children){
            if(collider instanceof AABB) continue;
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
                throw new RuntimeException(e);
            }
        }
    }

    private class CollisionManifoldStorage{

    }
}
