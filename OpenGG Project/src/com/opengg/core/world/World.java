/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.engine.OpenGG;
import static com.opengg.core.engine.OpenGG.processExecutables;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.RenderGroup;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.util.GGByteInputStream;
import com.opengg.core.util.GGByteOutputStream;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.physics.CollisionComponent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class World extends Component{
    public float floorLev = 0;
    public Vector3f gravityVector = new Vector3f(0,-9.81f,0);
    public List<RenderGroup> groups = new ArrayList<>();
    
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
        for(Component c : getAll()){
            if(c instanceof RenderComponent){
                addRenderable((RenderComponent)c);
            }
        }
    }
    
    public void addRenderable(RenderComponent r){
        if(OpenGG.hasExecutables())
            processExecutables();
        
        boolean found = false;
        for(RenderGroup rg : groups){
            if(rg.isTransparent() == r.isTransparent()){
                if(rg.getPipeline().equals(r.getShader())){
                    if(rg.getFormat().equals(r.getFormat())){
                        found = true;
                        rg.add(r);
                        break;
                    }
                }
            }            
        }
        
        if(!found){
            RenderGroup group = new RenderGroup("world " + r.getShader(), r.getFormat());
            group.add(r);
            group.setTransparent(r.isTransparent());
            group.setPipeline(r.getShader());
            groups.add(group);
            RenderEngine.addRenderGroup(group);
        }
    }
    
    public void removeRenderable(RenderComponent r){
        for(RenderGroup rg : groups){
            rg.remove(r);
        }
    }
    
    public LinkedList<CollisionComponent> useColliders() {
        LinkedList<CollisionComponent> list = new LinkedList<>();
        List<Component> components = getAll();
        for(Component c : components){
            if(c instanceof CollisionComponent){
                list.add((CollisionComponent)c);
            }
        }
        return list;
    }
    
    public void addCollider(CollisionComponent c){
        if(this != OpenGG.getCurrentWorld())
            return;
        WorldEngine.addCollider(c);
    }
    
    public void removeCollider(CollisionComponent c){
        WorldEngine.removeCollider(c);
    }
    
    private Component traverseFind(Component c, int i){
        if(c.getId() == i)
            return c;
        if(c instanceof Component){
            for(Component comp : ((Component)c).getChildren()){
                Component fc = traverseFind(comp, i);
                if(fc != null)
                    return fc;           
            }
        }
        return null;
    }
    
    private void traverseGet(Component c, List<Component> list){
        list.add(c);
        if(c instanceof Component){
            for(Component comp : ((Component)c).getChildren()){
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
    public void serialize(GGByteOutputStream out) throws IOException{
        super.serialize(out);
        out.write(gravityVector);
        out.write(floorLev);
    }
    
    @Override
    public void deserialize(GGByteInputStream in) throws IOException{
        super.deserialize(in);
        gravityVector = in.readVector3f();
        floorLev = in.readFloat();
    }
    
    @Override
    public World getWorld(){
        return this;
    }
}
