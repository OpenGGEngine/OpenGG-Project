package com.opengg.core.world.components;

import com.opengg.core.engine.Executor;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.WorldLoader;
import com.opengg.core.world.components.triggers.TriggerInfo;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

public class WorldChangeZone extends Zone {
    private String world;

    Function<TriggerInfo, Boolean> shouldExit = (data) -> data.data instanceof ControlledComponent;
    Consumer<TriggerInfo> onExit = (data) -> {};

    public WorldChangeZone(){
        world = "default";
    }

    public WorldChangeZone(String world, AABB box){
        super(box);
        this.world = world;
    }

    @Override
    public void onTrigger(TriggerInfo data){
        if(shouldExit.apply(data)){
            World newWorld = WorldLoader.getWorld(world);
            if(newWorld != null){
                Executor.async(() -> {
                    WorldEngine.useWorld(newWorld);
                    onExit.accept(data);
                });
            }
        }
    }

    public void setExitCondition(Function<TriggerInfo, Boolean> shouldExit){
        this.shouldExit = shouldExit;
    }

    public void setOnExit(Consumer<TriggerInfo> exit){
        onExit = exit;
    }

    public String getTargetWorld(){
        return world;
    }

    public void setWorld(String world) {
        System.out.println(world);
        this.world = world;
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(world);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        world = in.readString();
    }
}
