/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 *
 */
package com.opengg.core.engine;

import com.opengg.core.console.GGConsole;
import com.opengg.core.model.Animator;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;
import com.opengg.core.world.TransitionEngine;
import com.opengg.core.world.World;
import com.opengg.core.physics.collision.CollisionManager;
import com.opengg.core.world.WorldLoader;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.physics.CollisionComponent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

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
    
    public static void update(float delta){
        CollisionManager.clearCollisions();
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
    
    public static World loadWorld(String file){
        return WorldLoader.loadWorld(file);
    }
    
    public static void saveWorld(World world, String path){
        WorldLoader.saveWorld(world, path);
    }
     
    public static void useWorld(World world){
        world.rescanRenderables();
        removeRenderables(curworld);
        addRenderables(world);
        curworld = world;
        PhysicsEngine.setInstance(world.physics);
    }
    
    public static void addRenderables(World world){
        for(RenderGroup r : world.groups){
            RenderEngine.addRenderGroup(r);
        }
    }
    
    public static void removeRenderables(World world){
        for(RenderGroup r : world.groups){
            RenderEngine.removeRenderGroup(r);
        }
    }
    
    public static World getCurrent(){
        return curworld;
    }
}
