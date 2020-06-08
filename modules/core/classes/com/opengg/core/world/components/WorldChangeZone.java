package com.opengg.core.world.components;

import com.opengg.core.animation.Animation;
import com.opengg.core.animation.AnimationManager;
import com.opengg.core.engine.Executor;
import com.opengg.core.gui.GUIController;
import com.opengg.core.gui.UITexture;
import com.opengg.core.physics.collision.colliders.AABB;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.triggers.TriggerInfo;

import java.awt.*;
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
        if (shouldExit.apply(data)) {
            Animation sceneSwitch = new Animation(2.4f, false);
            sceneSwitch.addStaticEvent(Animation.AnimationStage.createStaticStage(0, 1f,
                    (d) -> Texture.ofColor(Color.BLACK, d.floatValue()),
                    (t) -> ((UITexture) GUIController.get("black").getRoot().getItem("tex")).setTexture(t))
                        .setOnComplete(() -> {
                            World newWorld = WorldEngine.getWorld(world);
                            Executor.async(() -> {
                                WorldEngine.setOnlyActiveWorld(newWorld);
                                onExit.accept(data);
                            });
                        }));

            sceneSwitch.addStaticEvent(Animation.AnimationStage.createStaticStage(1.4, 2.4f,
                    (d) -> Texture.ofColor(Color.BLACK, 1 - d.floatValue()),
                    (t) -> ((UITexture) GUIController.get("black").getRoot().getItem("tex")).setTexture(t))
                    .setUseLocalTimeReference(true));

            sceneSwitch.setToRun();
            AnimationManager.register(sceneSwitch);

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
