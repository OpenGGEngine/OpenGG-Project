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

    /**
     * Searches for any components in any registered world with the given name
     * @param name
     * @return List containing all found components
     */
    public static List<Component> findEverywhereByName(String name){
        return worlds.values().stream()
                .flatMap(w -> w.findByName(name).stream())
                .collect(Collectors.toList());
    }

    /**
     * Searches for the component given by its GUID in any registed world
     * @param guid
     * @return Optional containing the component with the given GUID, or an empty Optional
     */
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

    /**
     * Activates the given world in the World system, enabling it for updates <br>
     *     This runs all activation actions within the world, unless the world was already previously active, in which case
     *     it does nothing. <br>
     *     Additionally, this method also registers the world within the World system if not previously registered.
     * @param world World to activate
     * @see WorldEngine#registerWorld(World)
     */
    public static void activateWorld(World world){
        if(!isRegistered(world.getName()))
            registerWorld(world);
        if(world.isActive() == false){
            world.activate();
            worldActivationListeners.forEach(c -> c.accept(world));
        }
    }

    /**
     * Deactivates the given world in the World system, preventing any future updates <br>
     *     This method does nothing if the world is already inactive
     * @param world
     */
    public static void deactivateWorld(World world){
        if(world.isActive() == true)
            world.deactivate();
    }


    /**
     * Registers the given world with the WorldEngine <br>
     *     Registering refers to setting the given World class instance to be the instance in use by the engine. For
     *     example, loading a world from {@code foo.bwf} and registering the World instance from that file sets that as
     *     the instance of {@code foo.bwf} to use. Any future uses of {@code WorldEngine.getWorld("foo.bwf")} will return
     *     the instance of {@code foo.bwf} previously registered.
     *     <br> If the world has {@link World#shouldMultipleInstancesExist()} set to true, any future uses of
     *     {@code WorldEngine.getWorld("foo.bwf")} would return a new world, so to access an existing world, one would
     *     call {@code WorldEngine.getWorld} with the arguments being name + @ + world GUID. <br>
     *     In most cases, this is unnecessary, as {@link WorldEngine#getWorld(String)} automatically registers the world
     *     as needed.
     * @param world
     * @see World#shouldMultipleInstancesExist
     */
    public static void registerWorld(World world){
        worlds.put(world.getName(), world);
    }

    /**
     * Deactivates and removes the given world from the engine <br>
     *     This deregisters the world from the engine, allowing it to be garbage collected.
     * @param world World to remove
     */
    public static void deleteWorld(World world){
        deactivateWorld(world);
        worlds.remove(world.getName());
    }

    /**
     * Sets the given world as the primary world, and activates it if needed <br>
     *     The primary world is the world that is currently being rendered by the client.
     *     Although most components do not have separate behavior on primary vs non-primary worlds,
     *     some (such as {@link com.opengg.core.world.components.ControlledComponent} do), and must be accounted for
     *     when setting a primary world
     * @param world
     */
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

    /**
     * Sets the given world as the only active world <br>
     *     This sets the given world as the primary world, and deactivates (but doesn't deregister) all other loaded worlds
     * @param world
     */
    public static void setOnlyActiveWorld(World world){
        for(var existingWorld : worlds.values()){
            if(existingWorld != world) deactivateWorld(world);
        }

        setPrimaryWorld(world);
    }

    /**
     * Gets the world with the given name <br>
     *     This will attempt to find the world by the given name to see if it is already registered with the World system,
     *     and if it fails, will load the world from the file indicated by the {@code world} parameter.
     * @param world
     * @return World with given name from either WorldEngine registry or file
     * @see WorldEngine#registerWorld(World)
     * @throws IllegalArgumentException if the world argument provided is in the format used to reference specific instances
     *      of worlds with {@link World#shouldMultipleInstancesExist()} and the specific instance found does not exist
     */
    public static World getWorld(String world) {
        if(WorldEngine.isRegistered(world)) {
            return WorldEngine.getExistingWorld(world);
        }else {
            if(world.contains("@"))
                throw new IllegalArgumentException("Attempted to load world in multi-instance format, but world didn't exist! " +
                        "Failed to find world " + world);
            return WorldLoader.loadWorld(world);
        }
    }

    public static World getExistingWorld(String world){
        return worlds.get(world);
    }

    /**
     * Returns if the world given has a copy registered <br>
     *     For non-multiload worlds, this returns true if there
     * @param world
     * @return
     */
    public static boolean isRegistered(String world){
        return worlds.containsKey(world) && (worlds.get(world) != null);
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
