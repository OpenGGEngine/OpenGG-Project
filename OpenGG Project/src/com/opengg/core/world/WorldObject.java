/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.engine.UpdateEngine;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ComponentHolder;
import com.opengg.core.world.components.Positioned;
import com.opengg.core.world.components.Updatable;

/**
 *
 * @author Javier
 */
public class WorldObject extends ComponentHolder implements Positioned{
    public Vector3f pos = new Vector3f();
    public Quaternionf rot;
    private World thisWorld;
    public float mass;
    Component parent;
    public WorldObject(Vector3f pos, Quaternionf rot, World thisWorld) {
        this.pos = pos;
        this.rot = rot;
        this.thisWorld = thisWorld;
    }

    public WorldObject() {
        pos = new Vector3f(0, 0, 0);  
        rot = new Quaternionf();
    }

    @Override
    public void attach(Component c) {
        if(c instanceof Updatable){
            UpdateEngine.addObjects((Updatable) c);
        }
        c.setParentInfo(this);
        super.attach(c);
    }

    @Override
    public void setParentInfo(Component parent) {
        this.parent = parent;
    }

    @Override
    public void setPosition(Vector3f pos) {
        this.pos = pos;
    }

    @Override
    public void setRotation(Vector3f rot) {

    }

    @Override
    public Vector3f getPosition() {
        return pos;
    }

    @Override
    public Vector3f getRotation() {
        return new Vector3f();
    }
    
}
