/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resources;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class WorldLoader {
    public static World loadWorld(String worldname){
        GGConsole.log("Loading world " + worldname + "...");
        
        try (GGInputStream in = new GGInputStream(new FileInputStream(Resources.getAbsoluteFromLocal(worldname)))){
            int worldver = in.readInt();
            String ggversion = in.readString();
            
            int worldsize = in.readInt();
            byte[] worlddata = new byte[worldsize];
            for(int i = 0; i < worlddata.length; i++){
                worlddata[i] = in.readByte();
            }
            World w = Deserializer.deserialize(ByteBuffer.wrap(worlddata));
            GGConsole.log("World " + worldname + " has been successfully loaded");
            return w;
        } catch (FileNotFoundException ex) {
            GGConsole.error("Failed to find world named " + worldname);
        } catch (IOException ex) {
            GGConsole.error("Failed to access file named " + worldname);
        }
        return null;
        
    }
    
    public static void saveWorld(World world, String worldname){
        GGConsole.log("Saving world " + worldname + "...");
        try(GGOutputStream out = new GGOutputStream(new DataOutputStream(new FileOutputStream(Resources.getAbsoluteFromLocal(worldname))))) {
            out.write(1);
            out.write(GGInfo.getVersion());
            
            byte[] bworld = Serializer.serialize(world);
            out.write(bworld.length);
            out.write(bworld);
            out.flush();
        } catch (FileNotFoundException ex) {
            GGConsole.error("Failed to create file named " + worldname);
        } catch (IOException ex) {
            Logger.getLogger("Failed to write to file named " + worldname);
        }
        GGConsole.log("World " + worldname + " has been saved");
    }
}