/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.*;
import java.nio.ByteBuffer;

/**
 *
 * @author Javier
 */
public class WorldLoader {
    /**
     * Loads a world from a file
     * @param worldname World to be loaded
     * @return
     */
    public static World loadWorld(String worldname){
        GGConsole.log("Loading world " + worldname + "...");
        
        try (GGInputStream in = new GGInputStream(new FileInputStream(Resource.getWorldPath(worldname)))){
            int worldver = in.readInt();
            String ggversion = in.readString();

            int worldsize = in.readInt();
            byte[] worlddata = new byte[worldsize];
            for(int i = 0; i < worlddata.length; i++){
                worlddata[i] = in.readByte();
            }
            World w = Deserializer.deserialize(ByteBuffer.wrap(worlddata));
            w.setName(worldname);
            GGConsole.log("World " + worldname + " has been successfully loaded");
            return w;
        } catch (FileNotFoundException ex) {
            GGConsole.error("Failed to find world named " + worldname);
        } catch (IOException ex) {
            GGConsole.error("Failed to access file named " + worldname);
        }
        return null;
        
    }

    /**
     *
     * @param worldname
     * @return
     */
    public static World getWorld(String worldname){
        if(WorldStateManager.loadedVersionExists(worldname))
            return WorldStateManager.getLoadedWorld(worldname);
        else
            return loadWorld(worldname);
    }

    public static void keepWorld(World world){
        WorldStateManager.keepWorld(world);
    }
    
    public static void saveWorld(World world, String worldname){
        GGConsole.log("Saving world " + worldname + "...");
        String tempPath = worldname.substring(0, worldname.lastIndexOf(new File(worldname).getName())) + "temp_" + new File(worldname).getName();
        try(GGOutputStream out = new GGOutputStream(new DataOutputStream(new FileOutputStream(Resource.getAbsoluteFromLocal(tempPath))))) {
            out.write(1);
            out.write(GGInfo.getVersion());
            
            byte[] bworld = Serializer.serialize(world);
            out.write(bworld.length);
            out.write(bworld);
            out.flush();

            out.close();

            new File(Resource.getAbsoluteFromLocal(worldname)).delete();
            new File(Resource.getAbsoluteFromLocal(tempPath)).renameTo(new File(Resource.getAbsoluteFromLocal(worldname)));
        } catch (FileNotFoundException ex) {
            GGConsole.error("Failed to create file named " + worldname);
        } catch (IOException ex) {
            GGConsole.error("Failed to write to file named " + worldname);
        }
        GGConsole.log("World " + worldname + " has been saved");
    }

    private WorldLoader() {
    }
}
