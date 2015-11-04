/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.world.entities.*;
import com.opengg.core.render.DrawnObject;
import com.opengg.core.world.entities.Entity.EntityType;
import com.opengg.core.world.entities.EntityTypes;
import java.rmi.activation.ActivationException;

/**
 *
 * @author Javier
 */
public class WorldObject {
    private Vector3f pos;
    private Vector3f rot;
    private Entity e;
    private DrawnObject d;
    private World thisWorld;
    public WorldObject(Vector3f pos, Vector3f rot, OBJModel model, World thisWorld){
        this.pos = pos;
        this.rot = rot;
        this.thisWorld = thisWorld;
        try {
            e = EntityFactory.getEntity(EntityTypes.DEFAULT,EntityType.Static, pos, new Vector3f(), 10, model, thisWorld);
            e.setRotation(rot);
        } catch (ActivationException ex) {
            e = null;
        }
    }
    public WorldObject(){
        pos = new Vector3f(0,0,0);
        rot = new Vector3f(0,0,0);
        thisWorld = new World(new Camera(pos, rot));
        try {
            e = EntityFactory.getEntity(EntityTypes.DEFAULT, EntityType.Static, pos, new Vector3f(), 10, new OBJModel(), thisWorld);
            e.setXYZ(pos);
            e.setRotation(rot);
        } catch (ActivationException ex) {
            e = null;
        }
        this.thisWorld.addObject(this);
    }
    public WorldObject(Vector3f pos, Vector3f rot, Entity e){
        this.pos = pos;
        this.rot = rot;
        this.thisWorld = e.currentWorld;
        try {
            this.e = EntityFactory.getEntity(EntityTypes.DEFAULT,e);
            this.e.setXYZ(pos);
            this.e.setRotation(rot);
        } catch (ActivationException ex) {
            this.e = null;
        }
    }
    public WorldObject(Entity e){
        pos = new Vector3f(0,0,0);
        rot = new Vector3f(e.direction);
        this.thisWorld = e.currentWorld;
        try {
            this.e = EntityFactory.getEntity(EntityTypes.DEFAULT,e);;
            this.e.setXYZ(pos);
            this.e.setRotation(rot);
        } catch (ActivationException ex) {
            e = null;
        }
    }
    
    public void switchWorld(World next){
        thisWorld.removeObject(this);
        next.addObject(this);
        thisWorld = next;
        e.changeWorld(next);
    }
}
