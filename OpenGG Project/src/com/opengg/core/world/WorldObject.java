/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.Matrix4f;
import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector3f;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.util.GlobalInfo;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ComponentHolder;
import com.opengg.core.world.components.Positioned;
import com.opengg.core.world.components.Renderable;
import com.opengg.core.world.components.Updatable;

/**
 *
 * @author Javier
 */
public class WorldObject extends ComponentHolder implements Positioned, Updatable {

    private Vector3f target = new Vector3f();
    private Vector3f start = new Vector3f();
    private float speed;
    float elapsed = 0;
    public Vector3f pos = new Vector3f();
    private Matrix4f posmatrix;
    public Quaternion4f rot;
    private World thisWorld;
    public float mass;

    public WorldObject(Vector3f pos, Quaternion4f rot, OBJModel model, World thisWorld) {
        super();
        GlobalInfo.engine.addObjects(this);
        this.pos = pos;
        this.rot = rot;
        this.thisWorld = thisWorld;
    }

    public WorldObject() {
        super();
        GlobalInfo.engine.addObjects(this);
        pos = new Vector3f(0, 0, 0);  
        rot = new Quaternion4f();
    }

    @Override
    public void attach(Component c) {
        c.setParentInfo(this);
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

    /**
     * Returns the DrawnObject associated with this WorldObject !What a fail
     * javadoc pls!
     *
     * @return DrawnObject associated with this
     */
    @Override
    public void render() {
        for (Renderable r : renderable) {
            r.render();
        }
    }

    @Override
    public void update(float delta) {
        for (Updatable u : updateable) {
            u.update(delta);
        }
    }

    @Override
    public void setParentInfo(Component parent) {
        throw new InvalidParentException("WorldObjects cannot have parents!");
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
        //return rot;
        return new Vector3f();
    }

    public void move(Vector3f pos) {
        setPosition(pos);
    }
    /**
     *
     * Smoothly Moves A Object From One Place to Another
     * @param target Target Location
     * @param distanceperframe Speed where speed is 0 > 1.0
     */
    public void move(Vector3f target, float distanceperframe) {
        start = this.pos;
        this.target = target;
        this.speed = distanceperframe;
        
    }
}
