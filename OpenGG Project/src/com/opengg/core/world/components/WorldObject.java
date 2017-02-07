/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.engine.UpdateEngine;
import com.opengg.core.world.World;

/**
 *
 * @author Javier
 */
public class WorldObject extends ComponentHolder implements Positioned{
    public Vector3f pos;
    public Quaternionf rot;
    Component parent;
    
    public WorldObject(Vector3f pos, Quaternionf rot, World thisWorld) {
        this.pos = pos;
        this.rot = rot;
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
    public void setRotation(Quaternionf rot) {
        this.rot = rot;
    }

    @Override
    public Vector3f getPosition() {
        return pos;
    }

    @Override
    public Quaternionf getRotation() {
        return rot;
    }
    
}
