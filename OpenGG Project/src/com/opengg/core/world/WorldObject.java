/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.Matrix4f;
import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.world.entities.*;
import com.opengg.core.render.DrawnObject;
import com.opengg.core.world.entities.resources.EntitySupportEnums.PhysicsType;
import com.opengg.core.world.entities.EntityTypes;

/**
 *
 * @author Javier
 */
public class WorldObject {
    private Vector3f pos;
    private Quaternion4f rot;
    private Entity e;
    private DrawnObject d;
    private World thisWorld;
    public WorldObject(Vector3f pos, Quaternion4f rot, OBJModel model, World thisWorld){
        this.pos = pos;
        this.rot = rot;
        this.thisWorld = thisWorld;
        e = new EntityBuilder().physicsType(PhysicsType.Static).position(pos).model(model).world(thisWorld).build();
        e.setRotation(rot);
    }
    public WorldObject(){
        pos = new Vector3f(0,0,0);
        rot = new Quaternion4f();
        thisWorld = WorldManager.getDefaultWorld();
        e = new EntityBuilder().position(pos).build();
        e.setXYZ(pos);
        e.setRotation(rot);
        this.thisWorld.addObject(this);
    }
    public WorldObject(Vector3f pos, Quaternion4f rot, Entity e){
        this.pos = pos;
        this.rot = rot;
        this.thisWorld = e.currentWorld;
        this.e = new EntityBuilder(e).entityType(EntityTypes.DEFAULT).build();
        this.e.setXYZ(pos);
        this.e.setRotation(rot);
    }
    public WorldObject(Entity e){
        pos = new Vector3f(0,0,0);
        rot = new Quaternion4f(e.rot);
        this.thisWorld = e.currentWorld;
        this.e = new EntityBuilder(e).entityType(EntityTypes.DEFAULT).build();
        this.e.setXYZ(pos);
        this.e.setRotation(rot);
    }
    public WorldObject(DrawnObject d){
        pos = new Vector3f(0,0,0);
        rot = new Quaternion4f();
        thisWorld = WorldManager.getDefaultWorld();
        e = new EntityBuilder().physicsType(PhysicsType.Static).position(pos).world(thisWorld).build();
        e.setXYZ(pos);
        e.setRotation(rot);
        this.thisWorld.addObject(this);
        this.d = d;
    }
    
    /**
     * Changes position of Entity and DrawnObject
     * 
     * @param p New Position
     */
    
    public void setPos(Vector3f p){
        e.setXYZ(p);
        d.setModel(Matrix4f.translate(p.x, p.y, p.z));
    }
    
    /**
     * Changes rotation of Entity and DrawnObject
     * 
     * @param p New Rotation
     */
    
    public void setRot(Quaternion4f p){
        e.setRotation(p);
    }
    
    /**
     * Changes current World of WorldObject
     * 
     * @param next Next World
     */
    
    public void switchWorld(World next){
        thisWorld.removeObject(this);
        next.addObject(this);
        thisWorld = next;
        e.changeWorld(next);
    }
    
    /**
     * Returns the DrawnObject associated with this WorldObject
     * @return DrawnObject associated with this
     */
    public DrawnObject getDrawnObject(){
        return d;
    }
    
    /**
     * Gets Entity linked to this object
     * 
     */
    public Entity getEntity(){
        return e;
    }
}
