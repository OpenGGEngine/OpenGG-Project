/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector3f;
import com.opengg.core.components.Component;
import com.opengg.core.components.ModelRenderComponent;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.DrawnObjectGroup;
import com.opengg.core.world.entities.*;
import com.opengg.core.world.entities.EntityTypes;
import com.opengg.core.world.entities.resources.EntitySupportEnums.PhysicsType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class WorldObject implements Component {
    public Vector3f pos;
    public Quaternion4f rot;
    private Drawable d;
    private World thisWorld;
    public float mass;
    private List<Component> components = new ArrayList();
    public WorldObject(Vector3f pos, Quaternion4f rot, OBJModel model, World thisWorld){
        this.pos = pos;
        this.rot = rot;
        this.thisWorld = thisWorld;
    }
    public WorldObject(){
        pos = new Vector3f(0,0,0);
        rot = new Quaternion4f();
        thisWorld = WorldManager.getDefaultWorld();
        this.thisWorld.addObject(this);
    }
    public WorldObject(Vector3f pos, Quaternion4f rot, Entity e){
        this.pos = pos;
        this.rot = rot;
        this.thisWorld = e.current.currentWorld;
    }
    public WorldObject(DrawnObject d){
        pos = new Vector3f(0,0,0);
        rot = new Quaternion4f();
        thisWorld = WorldManager.getDefaultWorld();
        ModelRenderComponent a = new ModelRenderComponent(d);
        
        
        this.thisWorld.addObject(this);
       
    }
    public void attach(Component c){
        components.add(c);
    }
    public WorldObject(DrawnObjectGroup d){
        pos = new Vector3f(0,0,0);
        rot = new Quaternion4f();
        thisWorld = WorldManager.getDefaultWorld();
        this.thisWorld.addObject(this);
        ModelRenderComponent m = new ModelRenderComponent(d);
        components.add(m);
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
    public Drawable getDrawnObject(){
        return d;
    }
    
    public void attach(){
        
    }

    public void update(float delta) {
        
    }
}
