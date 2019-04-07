/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 *
 */
package com.opengg.core.world;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Javier Coindreau
 */
public class WorldEngine{
    private static World curworld;
    private static final List<Component> removal = new LinkedList<>();
    private static boolean enabled = true;

    private static List<Consumer<World>> worldChangeListeners = new ArrayList<>();
    private static List<Consumer<Component>> componentRemovalListenersS = new ArrayList<>();


    public static void initialize(){
        WorldEngine.useWorld(new World());
    }
    
    public static void markForRemoval(Component c){
        if(c != null)
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

    public static void addWorldChangeListener(Consumer<World> consumer){
        worldChangeListeners.add(consumer);
    }

    public static void addComponentRemovalListener(Consumer<Component> consumer){
        componentRemovalListenersS.add(consumer);
    }

    public static void removeMarked(){
        var tempremove = List.copyOf(removal);
        for(Component c : tempremove){
            remove(c);
            componentRemovalListenersS.forEach(cc -> cc.accept(c));
        }
        removal.clear();
    }
    
    private static void remove(Component c){
        c.finalizeComponent();
        if(c instanceof RenderComponent)
            c.getWorld().removeRenderable((RenderComponent)c);
        c.getParent().remove(c);
    }
    
    /**
     * Updates the WorldEngine (Including the TransitionEngine and the Animator)
     * @param delta Time since last update, in seconds
     */
    public static void update(float delta){
        removeMarked();
        if(enabled){
            WorldEngine.getCurrent().localUpdate(delta);
        }
    }

    /**
     * Runs all logic needed to switch the used {@link com.opengg.core.world.World}
     * from the current one to the given one
     * @param world World new World to use
     */
    public static void useWorld(World world){
        if(curworld != null)
            curworld.deactivate();

        world.rescanRenderables();
        world.use();
        curworld = world;

        worldChangeListeners.forEach(c -> c.accept(world));
    }

    /**
     * Returns the current world
     * @return Current world
     */

    public static World getCurrent(){
        return curworld;
    }

    private WorldEngine() {
    }
}
