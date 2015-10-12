/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import java.util.ArrayList;
import java.util.List;

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
    public static World getWorld(int world){
        int i = world;
        return worlds.get(i);
    }
    
}
