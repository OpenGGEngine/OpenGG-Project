package com.opengg.core.world;

import com.opengg.core.world.World;

import java.util.HashMap;
import java.util.Map;

public class WorldStateManager {
    private static Map<String, World> worlds = new HashMap<>();

    public static World getLoadedWorld(String world){
        return worlds.get(world);
    }

    public static boolean loadedVersionExists(String world){
        return worlds.containsKey(world) && (worlds.get(world) != null);
    }

    public static void keepWorld(World world){
        worlds.put(world.getName(), world);
    }
}
