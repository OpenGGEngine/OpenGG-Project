/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 *
 */
package com.opengg.core.engine;

import com.opengg.core.model.Animator;
import com.opengg.core.world.Deserializer;
import com.opengg.core.world.Serializer;
import com.opengg.core.world.TransitionEngine;
import com.opengg.core.world.World;
import com.opengg.core.physics.collision.CollisionHandler;
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
    static boolean enabled = true;
    
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
    
    public static void shouldUpdate(boolean update){
        enabled = update;
    }
    
    public static void removeMarked(){
        for(Component c : removal){
            if(c instanceof RenderComponent)
                c.getWorld().removeRenderable((RenderComponent)c);
            
            if(c instanceof CollisionComponent)
                colliders.remove((CollisionComponent)c);
            
            TransitionEngine.remove(c);
            c.getParent().remove(c);
            objs.remove(c);         
            c.finalizeComponent();
        }
        removal.clear();
    }
    
    public static void update(float delta){
        CollisionHandler.clearCollisions();
        removeMarked();
        Animator.update(delta);
        if(enabled){
            TransitionEngine.update(delta);
            traverseUpdate(WorldEngine.getCurrent(), delta);
        }
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
        try (DataInputStream dis = new DataInputStream(new FileInputStream(worldname))){
            int worldsize = dis.readInt();
            byte[] worlddata = new byte[worldsize];
            for(int i = 0; i < worlddata.length; i++){
                worlddata[i] = dis.readByte();
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
        try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(worldname))) {
            byte[] bworld = Serializer.serialize(world);
            dos.writeInt(bworld.length);
            dos.write(bworld);
            dos.flush();
        } catch (FileNotFoundException ex) {
            GGConsole.error("Failed to create file named " + worldname);
        } catch (IOException ex) {
            Logger.getLogger("Failed to write to file named " + worldname);
        }
        GGConsole.log("World " + worldname + " has been saved");
    }
    
    public static void useWorld(World world){
        OpenGG.curworld.disableRenderables();
        OpenGG.curworld = world;
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
