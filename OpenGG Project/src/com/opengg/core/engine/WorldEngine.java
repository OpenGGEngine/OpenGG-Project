/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 *
 */
package com.opengg.core.engine;

import com.opengg.core.util.Time;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;
import com.opengg.core.world.TransitionEngine;
import com.opengg.core.world.World;
import com.opengg.core.world.collision.CollisionHandler;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

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
    
    public static void update(float delta){
        CollisionHandler.clearCollisions();
        removeMarked();
        
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
    
    public static World loadWorld(String worldname){
        GGConsole.log("Loading world " + worldname + "...");
        try (DataInputStream dis = new DataInputStream(new FileInputStream(Resource.getLocal("resources\\worlds\\" + worldname + ".bwf")))){
            int worldsize = dis.readInt();
            byte[] worlddata = new byte[worldsize];
            for(int i = 0; i < worlddata.length; i++){
                worlddata[i] = dis.readByte();
            }
            World w = Deserializer.deserialize(ByteBuffer.wrap(worlddata));
            GGConsole.log("World " + worldname + " has been successfully loaded");
            return w;
        } catch (FileNotFoundException ex) {
            GGConsole.error("Failed to find world named " + worldname + " located at " + Resource.getLocal("resources\\worlds\\" + worldname + ".bwf"));
        } catch (IOException ex) {
            GGConsole.error("Failed to access file named " + Resource.getLocal("resources\\worlds\\" + worldname + ".bwf"));
        }
        return null;
    }
    
    public static void saveWorld(World world, String worldname){
        GGConsole.log("Saving world " + worldname + "...");
        try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(Resource.getLocal("resources\\worlds\\" + worldname + ".bwf")))) {
            byte[] bworld = Serializer.serialize(world);
            dos.writeInt(bworld.length);
            dos.write(bworld);
            dos.flush();
        } catch (FileNotFoundException ex) {
            GGConsole.error("Failed to create file named " + Resource.getLocal("resources\\worlds\\" + worldname + ".bwf"));
        } catch (IOException ex) {
            Logger.getLogger("Failed to write to file named " + Resource.getLocal("resources\\worlds\\" + worldname + ".bwf"));
        }
        GGConsole.log("World " + worldname + " has been saved");
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
