/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.world.entities.Entity;
import com.opengg.core.render.DrawnObject;
import com.opengg.core.world.entities.Entity.EntityType;
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
    private float floor;
    public WorldObject(Vector3f pos, Vector3f rot, OBJModel model, float floor){
        this.pos = pos;
        this.rot = rot;
        this.floor = floor;
        try {
            e = new Entity(EntityType.Static, pos.x, pos.y, pos.z, floor, rot, 10, 2, model);
        } catch (ActivationException ex) {
            e = null;
        }
    }
    public WorldObject(){
        pos = new Vector3f(0,0,0);
        rot = new Vector3f(0,0,0);
        floor = -1;
        try {
            e = new Entity(EntityType.Static, pos.x, pos.y, pos.z, floor, rot, 10, 2, new OBJModel());
        } catch (ActivationException ex) {
            e = null;
        }
    }
    public WorldObject(Vector3f pos, Vector3f rot, Entity e){
        this.pos = pos;
        this.rot = rot;
        try {
            this.e = new Entity(e);
        } catch (ActivationException ex) {
            e = null;
        }
        this.e.setXYZ(pos.x, pos.y, pos.z);
        this.e.setForce(rot);
    }
    public WorldObject(Entity e){
        pos = new Vector3f(0,0,0);
        rot = new Vector3f(0,0,0);
        try {
            this.e = new Entity(e);
        } catch (ActivationException ex) {
            e = null;
        }
    }
}
