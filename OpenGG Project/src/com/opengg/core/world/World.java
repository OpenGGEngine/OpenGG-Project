/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.physics.PhysicsSystem;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.RenderGroup;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
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
    public PhysicsSystem physics = new PhysicsSystem();
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
    
    public void rescanRenderables(){
        for(Component c : getAll()){
            if(c instanceof RenderComponent){
                addRenderable((RenderComponent)c);
            }
        }
    }
    
    public void addRenderable(RenderComponent r){
        boolean found = false;
        for(RenderGroup rg : groups){
            if(rg.isTransparent() == r.isTransparent()){
                if(rg.getPipeline().equals(r.getShader())){
                    if(rg.getFormat().equals(r.getFormat())){
                        if(!rg.getList().contains(r)){
                            rg.add(r);
                        }
                        found = true;
                        break;
                    }
                }
            }            
        }
        
        if(!found){
            RenderGroup group = new RenderGroup("world " + getId() + " " + r.getShader() + " " + r.getFormat().toString() + " group: " + (groups.size() + 1), r.getFormat());
            group.add(r);
            group.setTransparent(r.isTransparent());
            group.setPipeline(r.getShader());
            groups.add(group);
        }
        
        for(RenderGroup rg : groups){
            rg.setEnabled(true);
        }
    }
    
    public void removeRenderable(RenderComponent r){
        for(RenderGroup rg : groups){
            rg.remove(r);
        }
    }
    
    public PhysicsSystem getSystem(){
        return physics;
    }

    private Component traverseFind(Component c, int i){
        if(c.getId() == i)
            return c;
        if(c instanceof Component){
            for(Component comp : c.getChildren()){
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
            for(Component comp : c.getChildren()){
                traverseGet(comp, list);      
            }
        }
    }
    
    public void printLayout(){
        traversePrint(this,0);
    }
    
    private void traversePrint(Component c, int layer){
        String fin = "";
        
        for(int i = 0; i < layer; i++){
            fin += "  ";
        }
        
        fin += c.getName() + " : " + c.getClass().getSimpleName();
        
        for(Component comp : c.getChildren()){
            traversePrint(comp, layer + 1);
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
    public void onParentChange(Component parent) {
        throw new InvalidParentException("World must be the top level component!");
    }
    
    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(floorLev);
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        floorLev = in.readFloat();
    }
    
    @Override
    public World getWorld(){
        return this;
    }
}
