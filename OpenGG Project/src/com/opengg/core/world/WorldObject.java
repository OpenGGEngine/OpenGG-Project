/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector3f;
import com.opengg.core.engine.WorldManager;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ComponentHolder;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.Positioned;
import com.opengg.core.world.components.Renderable;

/**
 *
 * @author Javier
 */
public class WorldObject extends ComponentHolder implements Positioned {
    public Vector3f pos;
    public Quaternion4f rot;
    private World thisWorld;
    public float mass;
    
    public WorldObject(Vector3f pos, Quaternion4f rot, OBJModel model, World thisWorld){
        super();
        this.pos = pos;
        this.rot = rot;
        this.thisWorld = thisWorld;
    }
    public WorldObject(){
        super();
        pos = new Vector3f(0,0,0);
        rot = new Quaternion4f();
        thisWorld = WorldManager.getDefaultWorld();
        this.thisWorld.addObject(this);
    }
    public WorldObject(Drawable d){
        super();
        pos = new Vector3f(0,0,0);
        rot = new Quaternion4f();
        thisWorld = WorldManager.getDefaultWorld();
        ModelRenderComponent a = new ModelRenderComponent(d);
        
        
        this.thisWorld.addObject(this);
       
    }
    @Override
    public void attach(Component c){
        super.attach(c);
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
    }
    
    /**
     * Returns the DrawnObject associated with this WorldObject
     * @return DrawnObject associated with this
     */
    @Override
    public void render(){
        for(Renderable r : renderable){
            r.render();
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
}
