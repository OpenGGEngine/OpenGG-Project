/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 *
 */
package com.opengg.core.engine;

import com.opengg.core.util.Time;
import com.opengg.core.world.TransitionEngine;
import com.opengg.core.world.World;
import com.opengg.core.world.collision.CollisionHandler;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.physics.CollisionComponent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier Coindreau
 */
public class WorldEngine{
    static List<CollisionComponent> colliders = new LinkedList<>();
    static List<Component> objs = new ArrayList<>();
    static List<Component> removal = new LinkedList<>();
    static Time t;
    
    static{
        t = new Time();
    }
    
    public static void addCollider(CollisionComponent c) {
        colliders.add(c);
    }
    
    public static void removeCollider(CollisionComponent c){
        colliders.remove(c);
    }

    public static List<CollisionComponent> getColliders(){
        return colliders;
    }
    
    public static void markForRemoval(Component c){
        removal.add(c);
    }
    
    public static void addObjects(Component e){
        if(!objs.contains(e))
            objs.add(e);
    }
    
    public static void removeMarked(){
        for(Component c : removal){
            if(c instanceof RenderComponent)
                c.getWorld().removeRenderable((RenderComponent)c);
            
            if(c instanceof CollisionComponent)
                colliders.remove((CollisionComponent)c);
            
            TransitionEngine.remove(c);
            c.parent.remove(c);
            objs.remove(c);         
            c.remove();
        }
        removal.clear();
    }
    
    public static void update(){
        CollisionHandler.clearCollisions();
        removeMarked();
        float delta = t.getDeltaSec();
        TransitionEngine.update(delta);
        traverseUpdate(WorldEngine.getCurrent(), delta);
    }
    
    private static void traverseUpdate(Component c, float delta){
        if(!c.isEnabled() || ((c.updatedistance > c.getPosition().subtract(RenderEngine.camera.getPos()).length()) && c.updatedistance != 0))
            return;
        c.update(delta);
        for(Component c2 : ((Component)c).getChildren()){
            traverseUpdate(c2, delta);
        }
    }
    
    public static void useWorld(World w){
        OpenGG.curworld = w;
        rescanCurrent();
    }
    
    public static void rescanCurrent(){
        OpenGG.curworld.useRenderables();
        colliders = OpenGG.curworld.useColliders();
    }
    
    public static World getCurrent(){
        return OpenGG.curworld;
    }
}
