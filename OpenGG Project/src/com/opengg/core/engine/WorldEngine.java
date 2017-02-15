/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 *
 */
package com.opengg.core.engine;

import com.opengg.core.math.Vector3f;
import com.opengg.core.util.Time;
import com.opengg.core.world.World;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.physics.Collider;
import com.opengg.core.world.components.physics.CollisionData;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Javier Coindreau
 */
public class WorldEngine{
    static LinkedList<Collider> colliders = new LinkedList<>();
    static ArrayList<Component> objs = new ArrayList<>();
    static Time t;
    
    static{
        t = new Time();
    }
    
    static void processCollision(CollisionData info){
        if(info.c1physact){
            info.c1phys.velocity = new Vector3f(-info.c1phys.velocity.x, -info.c1phys.velocity.y, -info.c1phys.velocity.z);
        }
        if(info.c2physact){
            info.c2phys.velocity = new Vector3f(-info.c2phys.velocity.x, -info.c2phys.velocity.y, -info.c2phys.velocity.z);
        }
    }
    
    public static void checkColliders(){
        for(int i = 0; i < colliders.size(); i++){
            for(int j = i + 1; j < colliders.size(); j++){
                
                CollisionData info = colliders.get(i).testForCollision(colliders.get(j));
                if(info != null){
                    processCollision(info);
                }
            }
        }
    }
    
    public static void addObjects(Component e){
        objs.add(e);
    }
    
    public static void update(){
        float delta = t.getDeltaMs();
        objs.stream().forEach((e) -> {
            e.update(delta);
        });
        checkColliders();
    }
    
    private static ArrayList<World> worlds = new ArrayList();
    
    public static World getWorld(int world){
        return worlds.get(world);
    }
    
    public static World getDefaultWorld(){
        if (worlds.isEmpty())
            worlds.add(new World());
        return worlds.get(0);
    }
    
    public static boolean deleteWorld(int world){
        if(!worlds.get(world).getChildren().isEmpty())
            return false;
        worlds.remove(world);
        return true;
    }
    
    public static boolean deleteWorld(World world){
        if(!world.getChildren().isEmpty())
            return false;
        return worlds.remove(world);
    }
    
    public static boolean isEmpty(){
        return worlds.isEmpty();
    }

}
