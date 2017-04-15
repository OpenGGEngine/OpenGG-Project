/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 *
 */
package com.opengg.core.engine;

import com.opengg.core.render.Renderable;
import com.opengg.core.util.Time;
import com.opengg.core.world.World;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ComponentHolder;
import com.opengg.core.world.components.physics.PhysicsComponent;
import com.opengg.core.world.components.physics.collision.CollisionComponent;
import com.opengg.core.world.components.physics.collision.CollisionData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Javier Coindreau
 */
public class WorldEngine{
    static LinkedList<CollisionComponent> colliders = new LinkedList<>();
    static ArrayList<Component> objs = new ArrayList<>();
    static ArrayList<Component> removal = new ArrayList<>();
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
    
    static void processCollision(List<CollisionData> collisions){
        CollisionData data = collisions.get(0);
        PhysicsComponent component1 = data.collider1.getPhysicsComponent();
        PhysicsComponent component2 = data.collider2.getPhysicsComponent();

        if(component1 != null){
            component1.velocity = component1.velocity.reflect(data.collisionNormal);
        }
        if(component2 != null){
            component2.velocity = component2.velocity.reflect(data.collisionNormal);
        }
    }
    
    public static void checkColliders(){
        for(int i = 0; i < colliders.size(); i++){
            for(int j = i + 1; j < colliders.size(); j++){
                List<CollisionData> info = colliders.get(i).testForCollision(colliders.get(j));
                if(info == null || !info.isEmpty()){
                    processCollision(info);
                }
            }
        }
    }
    
    public static void markForRemoval(Component c){
        removal.add(c);
    }
    
    public static void addObjects(Component e){
        objs.add(e);
    }
    
    public static void removeMarked(){
        for(Component c : removal){
            if(c instanceof Renderable){
                c.getWorld().removeRenderable((Renderable)c);
            }
            
            if(c instanceof CollisionComponent){
                colliders.remove((CollisionComponent)c);
            }
            
            c.parent.remove(c);
            objs.remove(c);
            c.remove();
        }
        removal.clear();
    }
    
    public static void update(){
        checkColliders();
        removeMarked();
        float delta = t.getDeltaMs();
        Iterator<Component> iterator = OpenGG.curworld.getChildren().iterator();
        while(iterator.hasNext()){
            Component next = iterator.next();
            traverseUpdate(next, delta);
        }
    }
    
    private static void traverseUpdate(Component c, float delta){
        c.update(delta);
        if(c instanceof ComponentHolder){
            Iterator<Component> iterator = ((ComponentHolder)c).getChildren().iterator();
            while(iterator.hasNext()){
                Component next = iterator.next();
                traverseUpdate(next, delta);
            }
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
