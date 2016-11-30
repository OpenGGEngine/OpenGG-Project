/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.engine.EngineInfo;
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
    public Quaternion4f rot;
    private World thisWorld;
    public float mass;
    Component parent;
    public WorldObject(Vector3f pos, Quaternion4f rot, OBJModel model, World thisWorld) {
        this.pos = pos;
        this.rot = rot;
        this.thisWorld = thisWorld;
    }

    public WorldObject() {
        pos = new Vector3f(0, 0, 0);  
        rot = new Quaternion4f();
    }

    @Override
    public void attach(Component c) {
        if(c instanceof Updatable){
            EngineInfo.engine.addObjects((Updatable) c);
        }
        super.attach(c);
    }

    /**
     * Changes current World of WorldObject
     *
     * @param next Next World
     */
    public void switchWorld(World next) {
        thisWorld.removeObject(this);
        next.addObject(this);
        thisWorld = next;
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
