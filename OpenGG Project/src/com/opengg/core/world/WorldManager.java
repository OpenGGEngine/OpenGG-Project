/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class WorldManager {
    private static ArrayList<World> worlds = new ArrayList();
    private void WorldManager(){}
    public static World getWorld(Camera c){
        World w = new World(c);
        worlds.add(w);
        return w;
    }
    public static World getWorld(final int world){
        return worlds.get(world);
    }
    public static World getDefaultWorld(){
        if (worlds.isEmpty())
            worlds.add(new World());
        return worlds.get(0);
    }
    public static boolean deleteWorld(final int world){
        if(!worlds.get(world).getObjects().isEmpty())
            return false;
        worlds.remove(world);
        return true;
    }
    public static boolean deleteWorld(World world){
        if(!world.getObjects().isEmpty())
            return false;
        return worlds.remove(world);
    }
    public static boolean isEmpty(){
        return worlds.isEmpty();
    }
}
