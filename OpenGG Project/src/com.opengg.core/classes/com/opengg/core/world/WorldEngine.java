/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 *
 */
package com.opengg.core.world;

import com.opengg.core.console.GGConsole;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * @author Javier Coindreau
 */
public class WorldEngine{
    private static World primaryWorld;
    private static final List<Component> removal = new LinkedList<>();
    private static boolean enabled = true;

    private static List<Consumer<World>> worldActivationListeners = new ArrayList<>();
    private static List<Consumer<Component>> componentRemovalListenersS = new ArrayList<>();

    private static Map<String, World> worlds = new HashMap<>();

    public static void initialize(){
        WorldEngine.setOnlyActiveWorld(new World());
    }

    /**
     * Sets if the WorldEngine should run 
     * @param update If it should update
     */
    public static void shouldUpdate(boolean update){
        enabled = update;
    }

    public static void addWorldActivationListener(Consumer<World> consumer){
        worldActivationListeners.add(consumer);
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

    public static List<Component> findEverywhereByName(String name){
        return worlds.values().stream()
                .flatMap(w -> w.findByName(name).stream())
                .collect(Collectors.toList());
    }

    public static Optional<Component> findEverywherByGUID(long guid){
        return worlds.values().stream()
                .flatMap(w -> w.findByGUID(guid).stream())
                .findFirst();
    }

    /**
     * Updates the WorldEngine (Including the TransitionEngine and the Animator)
     * @param delta Time since last update, in seconds
     */
    public static void update(float delta){
        removeMarked();
        if(enabled){
            WorldEngine.worlds.values().forEach(w -> w.localUpdate(delta));
        }
    }

    public static void activateWorld(World world){
        if(!hasExistingCopy(world.getName()))
            registerWorld(world);
        if(world.isActive() == false){
            world.activate();
            worldActivationListeners.forEach(c -> c.accept(world));
        }
    }

    public static void deactivateWorld(World world){
        world.deactivate();
    }

    public static void deleteWorld(World world){
        deactivateWorld(world);
        deregisterWorld(world);
    }

    public static void setPrimaryWorld(World world){
        if(primaryWorld == world) return;
        if(primaryWorld != null){
            primaryWorld.removeAsPrimary();
        }
        activateWorld(world);
        GGConsole.log("Changing primary world to " + world.getName() + ":" + world.getGUID());
        primaryWorld = world;
        world.setAsPrimary();
    }

    public static void setOnlyActiveWorld(World world){
        for(var existingWorld : worlds.values()){
            if(existingWorld != world) deactivateWorld(world);
        }

        setPrimaryWorld(world);
    }

    public static World getExistingWorld(String world){
        return worlds.get(world);
    }

    public static boolean hasExistingCopy(String world){
        return worlds.containsKey(world) && (worlds.get(world) != null);
    }

    public static void registerWorld(World world){
        worlds.put(world.getName(), world);
    }

    private static void deregisterWorld(World world){
        worlds.remove(world.getName());
    }

    public static void markComponentForRemoval(Component c){
        if(c != null)
            iterateMark(c);
    }

    private static void iterateMark(Component c){
        for(Component cc : c.getChildren()){
            iterateMark(cc);
        }
        removal.add(c);
    }

    private static void remove(Component c){
        c.finalizeComponent();
        if(c instanceof RenderComponent)
            c.getWorld().removeRenderable((RenderComponent)c);
        c.getParent().remove(c);
    }

    /**
     * Returns the current world
     * @return Current world
     */

    public static World getCurrent(){
        return primaryWorld;
    }

    private WorldEngine() {
    }
}
