/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.Matrix4f;
import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector3f;
import com.opengg.core.components.Component;
import com.opengg.core.components.ModelRenderComponent;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.DrawnObjectGroup;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.entities.*;
import com.opengg.core.world.entities.EntityTypes;
import com.opengg.core.world.entities.resources.EntitySupportEnums.PhysicsType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class WorldObject {
    private Vector3f pos;
    private Quaternion4f rot;
    private Entity e;
    private Drawable d;
    private World thisWorld;
    private List<Component> components = new ArrayList();
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
        this.thisWorld = e.current.currentWorld;

        this.e = new EntityBuilder(e).entityType(EntityTypes.DEFAULT).build();
        this.e.setXYZ(pos);
        this.e.setRotation(rot);
    }
    public WorldObject(Entity e){
        pos = new Vector3f(0,0,0);
        rot = new Quaternion4f(e.current.rot);

        this.thisWorld = e.current.currentWorld;
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
        e = new EntityBuilder().physicsType(PhysicsType.Static).position(pos).world(thisWorld).build();
        e.setXYZ(pos);
        e.setRotation(rot);
        this.thisWorld.addObject(this);
        ModelRenderComponent m = new ModelRenderComponent(d);
        components.add(m);
    }
    
    /**
     * Changes position of Entity and DrawnObject
     * 
     * @param p New Position
     */
    
    public void setPos(Vector3f p){
        e.setXYZ(p);
        //.setMatrix(Matrix4f.translate(p.x, p.y, p.z));
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
    public Drawable getDrawnObject(){
        return d;
    }
    public void render(){
        components.stream().forEach((c) -> {
            c.render();
        });
    }
    /**
     * Gets Entity linked to this object
     * 
     */
    public Entity getEntity(){
        return e;
    }
    
    public void attach(){
        
    }
}
