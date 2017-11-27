/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.entities;

import com.opengg.core.math.Vector3f;
import com.opengg.core.world.World;
import com.opengg.core.world.entities.resources.EntitySupportEnums.PhysicsType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ethachu19
 */
public class EntityBuilder {
//
//    /**
//     * Number of Entities currently loaded.
//     */
//    public static int entityCount = 0;
//
//    /**
//     * List of currently loaded entities
//     */
//    protected static final List<Entity> EntityList = new ArrayList<>();
//    public static final List<Entity> AddStack = new ArrayList<>();
//    /**
//     * Amount of Entities
//     */
//    protected static int entityCap = 44;
//
//    private EntityTypes et = EntityTypes.DEFAULT;
//    private PhysicsType pt = PhysicsType.Physics;
//    private Vector3f pos = new Vector3f();
//    private Vector3f f = new Vector3f();
//    private float mass = 10f;
//    private OBJModel model = new OBJModel();
//    private World thisWorld;
//
//    private Entity copy = null;
//
//    public EntityBuilder() {
//    }
//
//    public EntityBuilder(Entity copy) {
//        this.copy = copy;
//    }
//
//    public EntityBuilder entityType(EntityTypes et) {
//        this.et = et;
//        return this;
//    }
//
//    public EntityBuilder physicsType(PhysicsType pt) {
//        this.pt = pt;
//        return this;
//    }
//
//    public EntityBuilder position(Vector3f pos) {
//        this.pos = pos;
//        return this;
//    }
//
//    public EntityBuilder force(Vector3f f) {
//        this.f = f;
//        return this;
//    }
//
//    public EntityBuilder mass(float mass) {
//        this.mass = mass;
//        return this;
//    }
//
//    public EntityBuilder model(OBJModel model) {
//        this.model = model;
//        return this;
//    }
//
//    public EntityBuilder world(World thisWorld) {
//        this.thisWorld = thisWorld;
//        return this;
//    }
//
//    public Entity build() {
//        if (entityCount >= entityCap) {
//            return null;
//        }
//        ++entityCount;
//        if (copy != null) {
//            switch (et) {
//                case PLAYER:
//                    return new PlayerEntity(copy);
//                default:
//                    return new Entity(copy);
//            }
//        }
//        switch (et) {
//            case PLAYER:
//                return new PlayerEntity(pt, pos, f, mass, model, thisWorld);
//            default:
//                return new Entity(pt, pos, f, mass, model, thisWorld);
//        }
//    }
//
//    /**
//     * Destroys entity given
//     *
//     * @param des Entity to be destroyed
//     * @return Error
//     */
//    public static final boolean destroyEntity(Entity des) {
//        des = null;
//        return EntityList.remove(des);
//    }
//
//    /**
//     * Destroys entity given
//     *
//     * @param i Index of entity to be destroyed
//     * @return Error
//     */
//    public static final boolean destroyEntity(int i) {
//        Entity des = EntityList.get(i);
//        des = null;
//        return EntityList.remove(des);
//    }
}
