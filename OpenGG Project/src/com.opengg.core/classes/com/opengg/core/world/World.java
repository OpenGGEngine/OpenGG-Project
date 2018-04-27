/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.physics.PhysicsSystem;
import com.opengg.core.engine.RenderGroup;
import com.opengg.core.exceptions.InvalidParentException;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <h1>Represents a world, the highest level object in the component/world system</h1>
 * 
 * It contains all of the information required to run a specific instance of the world system.
 * This includes the related instance of the physics engine and all of the render groups for this specific world 
 * <p>
 * Note, while this is a subclass of component for the purpose of implementation and abstraction, many functions related to
 * positioning and parents either return default values or throw errors, an example of the latter 
 * being {@link com.opengg.core.world.components.Component#getParentInfo()). Otherwise, 
 * commands like {@link com.opengg.core.world.components.Component#attach() }work as intended
 * @author Javier
 */
public class World extends Component{
    public PhysicsSystem physics = new PhysicsSystem();
    public List<RenderGroup> groups = new ArrayList<>();
    public Skybox skybox;
    
    
    /**
     * Returns all components attached to this world
     * @return All components in this world
     */
    public List<Component> getAll(){
        List<Component> components = new LinkedList<>();
        for(Component c : this.getChildren()){
            traverseGet(c, components);
        }
        return components;
    }
    
    private void traverseGet(Component c, List<Component> list){
        list.add(c);
        if(c instanceof Component){
            for(Component comp : c.getChildren()){
                traverseGet(comp, list);      
            }
        }
    }
    
    /**
     * Finds the unique {@link com.opengg.core.world.components.Component} with a certain ID, returns {@code null}  if none are found
     * @param id ID being searched for
     * @return Component with given ID or {@code null}  if nonexistent
     */
    public Component find(int id){
        Component c = null;
        for(Component cc : this.getChildren()){
            Component ccc = traverseFind(cc, id);
            if(ccc != null){
                return ccc;
            }
        }
        return null;
    }
    
    /**
     * Finds the first {@link com.opengg.core.world.components.Component} with a certain name, returns {@code null} if none are found
     * @param name Name being searched for
     * @return Component with given name or {@code null}  if nonexistent
     */
    public Component find(String name){
        Component c = null;
        for(Component cc : this.getChildren()){
            Component ccc = traverseFind(cc, name);
            if(ccc != null){
                return ccc;
            }
        }
        return null;
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
    
    private Component traverseFind(Component c, String s){
        if(c.getName().equals(s))
            return c;
        if(c instanceof Component){
            for(Component comp : c.getChildren()){
                Component fc = traverseFind(comp,s);
                if(fc != null)
                    return fc;           
            }
        }
        return null;
    }
    
    /**
     * Regenerates the render groups for this world and sends them to {@link com.opengg.core.engine.RenderEngine}, 
     * should only be used if adding many renderables. Otherwise, use {@link #addRenderable(com.opengg.core.world.components.RenderComponent) }
     */
    public void rescanRenderables(){
        for(Component c : getAll()){
            if(c instanceof RenderComponent){
                addRenderable((RenderComponent)c);
            }
        }
    }
    
    /**
     * Adds a {@link com.opengg.core.world.components.RenderComponent} to the World's {@link com.opengg.core.engine.RenderGroup} depending on certain values, 
     * and creates a new group if none that fit the components traits are found
     * @param renderable 
     */
    public void addRenderable(RenderComponent renderable){
        boolean found = false;
        for(RenderGroup rg : groups){
            if(rg.isTransparent() == renderable.isTransparent()){
                if(rg.getPipeline().equals(renderable.getShader())){
                    if(rg.getFormat().equals(renderable.getFormat())){
                        if(!rg.getList().contains(renderable)){
                            rg.add(renderable);
                        }
                        found = true;
                        break;
                    }
                }
            }            
        }
        
        if(!found){
            RenderGroup group = new RenderGroup("world " + getId() + " " + renderable.getShader() + " " 
                    + renderable.getFormat().toString() + " group: " + (groups.size() + 1), renderable.getFormat());
            group.add(renderable);
            group.setTransparent(renderable.isTransparent());
            group.setPipeline(renderable.getShader());
            groups.add(group);
        }
        
        for(RenderGroup rg : groups){
            rg.setEnabled(true);
        }
    }
    
    /**
     * Removes a {@link com.opengg.core.world.components.RenderComponent} from the World's {@link com.opengg.core.engine.RenderGroup}s
     * @param r RenderComponent to be removed
     */
    public void removeRenderable(RenderComponent r){
        for(RenderGroup rg : groups){
            rg.remove(r);
        }
    }
    
    /**
     * Returns the {@link com.opengg.core.physics.PhysicsSystem} associated with the World
     * @return PhysicsSystem associated with the world
     */
    public PhysicsSystem getSystem(){
        return physics;
    } 

    /**
     * Recursively prints the Component layout of this world to the default {@link java.io.PrintStream}
     */
    public void printLayout(){
    }
    
    private String traversePrint(Component c){
        String fin = "";
        
        fin += c.getName() + " : " + c.getClass().getSimpleName();
        
        for(Component comp : c.getChildren()){
            fin += " "  + traversePrint(comp) + " \n";
        }
        return fin;
    }
    
    /**
     * Overrides {@link com.opengg.core.world.components.Component#getPosition()}, always returns 0,0,0
     * @return returns Vector3f containing 0,0,0
     */
    @Override
    public Vector3f getPosition(){
        return new Vector3f();
    }
    
    /**
     * Overrides {@link com.opengg.core.world.components.Component#getRotation()}, always returns a unit quaternion
     * @return returns a Quaternionf representing no rotation
     */
    @Override
    public Quaternionf getRotation(){
        return new Quaternionf();
    }

    /**
     * Overrides {@link com.opengg.core.world.components.Component#getScale()}, always returns 1,1,1
     * @return returns Vector3f containing 1,1,1
     */
    @Override
    public Vector3f getScale(){
        return new Vector3f(1,1,1);
    }
    
    /**
     * Overrides {@link com.opengg.core.world.components.Component#onParentChange()}, throws an exception to
     * prevent attachment to any other components
     * @throws com.opengg.core.exceptions.InvalidParentException 
     */
    @Override
    public void onParentChange(Component parent) {
        throw new InvalidParentException("World must be the top level component!");
    }
    
    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        for(TextureData data : skybox.cube.getData()){
            out.write(data.source);
        }
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
    }
    
    /**
     * Overrides {@link com.opengg.core.world.components.Component#getWorld()}, returns this world
     * @return returns this world
     */
    @Override
    public World getWorld(){
        return this;
    }
}
