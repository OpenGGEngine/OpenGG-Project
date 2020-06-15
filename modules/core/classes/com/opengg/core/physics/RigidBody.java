/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics;

import com.opengg.core.console.GGConsole;
import com.opengg.core.exceptions.ClassInstantiationException;
import com.opengg.core.math.util.Tuple;
import com.opengg.core.physics.collision.Collider;
import com.opengg.core.physics.collision.CollisionManager;
import com.opengg.core.physics.collision.ContactManifold;
import com.opengg.core.physics.collision.colliders.AABB;
import com.opengg.core.util.ClassUtil;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.physics.RigidBodyComponent;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import com.opengg.core.world.components.triggers.Triggerable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class RigidBody extends PhysicsObject implements Trigger {
    private final ArrayList<Triggerable> subscribers = new ArrayList<>();
    private final List<Collider> colliders = new ArrayList<>();
    private PhysicsProvider physicsProvider;
    private AABB aabb;

    private RigidBodyComponent componentOwner;

    public float charge = 0f;

    public float dynamicfriction = 0.3f;
    public float staticfriction = 0.4f;
    public float restitution = 0.5f;

    public RigidBody(){
        this(new AABB(1,1,1), new ArrayList<>());
    }

    public RigidBody(AABB main, Collider... all) {
        this(main, Arrays.asList(all));
    }

    public RigidBody(AABB main, List<? extends Collider> all) {
        setBoundingBox(main);
        addColliders(all);
    }
    
    public void addCollider(Collider collider) {
        children.add(collider);
        colliders.add(collider);
        collider.setParent(this);
    }
    
    public void addColliders(List<? extends Collider> bb) {
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

    public Optional<PhysicsProvider> getPhysicsProvider(){
        return Optional.ofNullable(physicsProvider);
    }

    public boolean hasPhysicsProvider(){
        return  physicsProvider != null;
    }

    public void enablePhysicsProvider(){
        if(!hasPhysicsProvider()) physicsProvider = new PhysicsProvider(this);
    }

    public boolean boundToComponent(){
        return componentOwner != null;
    }

    public void bindToComponent(RigidBodyComponent component){
        componentOwner = component;
    }

    public RigidBodyComponent getComponentOwner(){
        return componentOwner;
    }

    public Optional<ContactManifold> checkForCollision(RigidBody other) {
        this.aabb.recalculate();
        other.aabb.recalculate();
        if (!aabb.isColliding(other.aabb)) {
            return Optional.empty();
        }
        this.getColliders().forEach(Collider::updatePositions);
        other.getColliders().forEach(Collider::updatePositions);

        var collisions = this.getColliders().stream()
                .flatMap(s -> other.getColliders().stream()
                            .map(s2 -> Tuple.ofUnordered(s,s2)))
                .distinct()
                .map(t -> t.x().collide(t.y()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        var finalManifolds = collisions.stream()
                .flatMap(Optional::stream)
                .peek(c -> c.contactsInUse.forEach(cc -> cc.timestamp = this.getSystem().getTick()))
                .collect(Collectors.toList());
        if(finalManifolds.isEmpty()) return Optional.empty();

        var finalManifold = ContactManifold.combineManifolds(finalManifolds);
        var highestDepth =  finalManifold.contactsInUse.stream().max(Comparator.comparingDouble(c -> c.depth)).get();

        if(this.hasPhysicsProvider()){
            this.setPosition(this.getPosition().add(highestDepth.normal.multiply(highestDepth.depth)));
        }
        if(other.hasPhysicsProvider()){
            other.setPosition(other.getPosition().add(highestDepth.normal.inverse().multiply(highestDepth.depth)));
        }

        this.subscribers.forEach(
                t -> t.onTrigger(this, new TriggerInfo(other, "", TriggerInfo.TriggerType.SINGLE))
        );
        other.subscribers.forEach(
                t -> t.onTrigger(other, new TriggerInfo(this, "", TriggerInfo.TriggerType.SINGLE))
        );

        return Optional.of(finalManifold);
    }

    @Override
    public void update(float delta){
        CollisionManager.addToTest(this);
        if(hasPhysicsProvider()) physicsProvider.update(delta);
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        out.write(id);

        out.write(staticfriction);
        out.write(dynamicfriction);
        out.write(restitution);

        out.write(this.children.size());
        for(var collider : children){
            out.write(collider.getClass().getName());
            collider.serialize(out);
        }
        out.write(hasPhysicsProvider());
        if(hasPhysicsProvider()){
            getPhysicsProvider().get().serialize(out);
        }

    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        id = in.readLong();

        staticfriction = in.readFloat();
        dynamicfriction = in.readFloat();
        restitution = in.readFloat();

        var colsize = in.readInt();
        for(int i = 0; i < colsize; i++){
            var classname = in.readString();
            try {
                var object = (PhysicsObject) ClassUtil.createByName(classname);
                object.deserialize(in);
                if(object instanceof AABB)
                    setBoundingBox((AABB) object);
                else
                    addCollider((Collider) object);
            } catch (ClassInstantiationException e) {
                GGConsole.error("Failed to insantiate collider with classname " + classname + ": " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        var hasPhysics = in.readBoolean();
        if(hasPhysics){
            this.enablePhysicsProvider();
            this.getPhysicsProvider().get().deserialize(in);
        }
    }

    public void serializeUpdate(GGOutputStream out) throws IOException{
        if(hasPhysicsProvider()){
            physicsProvider.serializeUpdate(out);
        }
    }

    public void deserializeUpdate(GGInputStream in, float delta) throws IOException{
        if(hasPhysicsProvider()){
            physicsProvider.deserializeUpdate(in, delta);
        }
    }

    @Override
    public void addListener(Triggerable dest) {
        this.subscribers.add(dest);
    }
}
