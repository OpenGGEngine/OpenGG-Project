package com.opengg.test.network.components;

import com.opengg.core.audio.Sound;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.objects.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.RenderComponent;

import java.awt.*;

public class GrenadeItem extends Item {
    private Sound gunfire;
    private RenderComponent view;

    public GrenadeItem() {
        super("Grenade");
        setView(generateView());
        //gunfire = new Sound(Resource.getSoundData("45acp.ogg"));
    }

    @Override
    public RenderComponent generateView(){
        return new RenderComponent(new TextureRenderable(
                ObjectCreator.createQuadPrism(new Vector3f(-0.15f, -0.15f, -0.15f),
                        new Vector3f(0.15f, 0.15f, 0.15f)),
                Texture.ofColor(Color.DARK_GRAY)));
    }

    @Override
    public boolean use() {
        Grenade grenade = new Grenade(this);
        WorldEngine.getCurrent().attach(grenade);
        return true;
    }

}
