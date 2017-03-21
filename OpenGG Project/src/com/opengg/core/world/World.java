/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.RenderGroup;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ComponentHolder;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.physics.collision.CollisionComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class World extends ComponentHolder{
    public float floorLev = 0;
    public Vector3f gravityVector = new Vector3f(0,-9.81f,0);
    public RenderGroup group = new RenderGroup();
    public RenderGroup groupnoadj = new RenderGroup();
    
    public void setFloor(float floor){
        floorLev = floor;
    }
    
    public List<Component> getAll(){
        List<Component> components = new LinkedList<>();
        for(Component c : this.getChildren()){
            traverseGet(c, components);
        }
        return components;
    }
    
    public Component find(int i){
        Component c = null;
        for(Component cc : this.getChildren()){
            Component ccc = traverseFind(cc, i);
            if(ccc != null){
                return ccc;
            }
        }
        return null;
    }
    
    public void useRenderables(){
        group.setAdjacencyMesh(true);
        groupnoadj.setAdjacencyMesh(false);  
        for(Component c : getAll()){
            if(c instanceof Renderable){
                if(((Renderable)c) instanceof RenderComponent){
                    if(((RenderComponent)c).getDrawable() instanceof DrawnObject){
                        if(((DrawnObject)(((RenderComponent)c).getDrawable())).hasAdjacency()){
                            groupnoadj.add(((DrawnObject)(((RenderComponent)c).getDrawable())));
                            continue;
                        }
                    }
                }
                group.add((Renderable)c);
            }
        }

        
        RenderEngine.addRenderGroup(group);
        RenderEngine.addRenderGroup(groupnoadj);
    }
    
    public LinkedList<CollisionComponent> useColliders() {
        LinkedList<CollisionComponent> list = new LinkedList<>();
        List<Component> components = getAll();
        for(Component c : components){
            if(c instanceof PhysicsComponent){
                List<CollisionComponent> collide = ((PhysicsComponent) c).getColliders();
                if (collide != null){
                    list.addAll(collide);
                }
            }
        }
        return list;
    }
    
    private Component traverseFind(Component c, int i){
        if(c.id == i)
            return c;
        if(c instanceof ComponentHolder){
            for(Component comp : ((ComponentHolder)c).getChildren()){
                Component fc = traverseFind(comp, i);
                if(fc != null)
                    return fc;           
            }
        }
        return null;
    }
    
    private void traverseGet(Component c, List<Component> list){
        list.add(c);
        if(c instanceof ComponentHolder){
            for(Component comp : ((ComponentHolder)c).getChildren()){
                traverseGet(comp, list);      
            }
        }
    }
    
    @Override
    public Vector3f getPosition(){
        return new Vector3f();
    }
    
    @Override
    public Quaternionf getRotation(){
        return new Quaternionf();
    }

    @Override
    public Vector3f getScale(){
        return new Vector3f(1,1,1);
    }
    
    @Override
    public void setParentInfo(Component parent) {
        throw new InvalidParentException("World must be the top level component!");
    }
    
    @Override
    public void serialize(Serializer s){
        super.serialize(s);
        s.add(gravityVector);
        s.add(floorLev);
    }
    
    @Override
    public void deserialize(Deserializer ds){
        super.deserialize(ds);
        gravityVector = ds.getVector3f();
        floorLev = ds.getFloat();
    }
    
    @Override
    public World getWorld(){
        return this;
    }
}
