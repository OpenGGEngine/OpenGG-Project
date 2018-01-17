/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 *
 */
package com.opengg.core.engine;

import com.opengg.core.model.Animator;
import com.opengg.core.world.TransitionEngine;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldLoader;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier Coindreau
 */
public class WorldEngine{
    private static World curworld;
    private static List<Component> removal = new LinkedList<>();
    private static boolean enabled = true;
    
    public static void initialize(){
        curworld = new World();
    }
    
    public static void markForRemoval(Component c){
        iterateMark(c);
    }
    
    private static void iterateMark(Component c){
        for(Component cc : c.getChildren()){
            iterateMark(cc);
        }
        removal.add(c);
    }
    
    /**
     * Sets if the WorldEngine should run 
     * @param update If it should update
     */
    public static void shouldUpdate(boolean update){
        enabled = update;
    }
    
    public static void removeMarked(){
        for(Component c : removal){
            remove(c);  
        }
        removal.clear();
    }
    
    private static void remove(Component c){
        c.finalizeComponent();
        if(c instanceof RenderComponent)
            c.getWorld().removeRenderable((RenderComponent)c);
        TransitionEngine.remove(c); 
        c.getParent().remove(c);
    }
    
    /**
     * Updates the WorldEngine (Including the TransitionEngine and the Animator)
     * @param delta Time since last update, in seconds
     */
    public static void update(float delta){
        removeMarked();
        Animator.update(delta);
        if(enabled){
            TransitionEngine.update(delta);
            traverseUpdate(WorldEngine.getCurrent(), delta);
        }
    }
    
    private static void traverseUpdate(Component c, float delta){
        if(!c.isEnabled() || ((c.updatedistance > c.getPosition().subtract(RenderEngine.getCurrentCamera().getPos()).length()) && c.updatedistance != 0))
            return;
        c.update(delta);
        for(Component c2 : ((Component)c).getChildren()){
            traverseUpdate(c2, delta);
        }
    }
    
    /**
     * Loads {@link com.opengg.core.world.World} from a file located in the resources\worlds directory
     * in the Binary World Format (with a .bwf extension)
     * @param file Filename of world, not including the path up to resources\worlds
     * @return Loaded world
     */
    public static World loadWorld(String file){
        return WorldLoader.loadWorld(file);
    }
    
    /**
     * Saves the given {@link com.opengg.core.world.World} to the given filename (Should not include the path)
     * @param world World to save
     * @param path Filename, should be formatted somename.bwf
     */
    public static void saveWorld(World world, String path){
        WorldLoader.saveWorld(world, path);
    }
     
    /**
     * Runs all logic needed to switch the used {@link com.opengg.core.world.World}
     * from the current one to the given one
     * @param world World new World to use
     */
    public static void useWorld(World world){
        world.rescanRenderables();
        removeRenderables(curworld);
        addRenderables(world);
        curworld = world;
        PhysicsEngine.setInstance(world.physics);
    }
    
    /**
     * Adds all of the render groups from the given {@link com.opengg.core.world.World}
     * to be rendered
     * @param world World to get renderables from
     */
    public static void addRenderables(World world){
        for(RenderGroup r : world.groups){
            RenderEngine.addRenderGroup(r);
        }
    }
    
    /**
     * Removes all of the render groups from the given {@link com.opengg.core.world.World}
     * @param world World to remove renderables from
     */
    public static void removeRenderables(World world){
        for(RenderGroup r : world.groups){
            RenderEngine.removeRenderGroup(r);
        }
    }
    
    /**
     * Returns the current world
     * @return Current world
     */
    public static World getCurrent(){
        return curworld;
    }
}
