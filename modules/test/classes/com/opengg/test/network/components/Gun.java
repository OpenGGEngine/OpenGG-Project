/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.test.network.components;

import com.opengg.core.audio.Sound;
import com.opengg.core.engine.Resource;
import com.opengg.core.render.objects.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.RenderComponent;

import java.awt.*;

/**
 *
 * @author Javier
 */
public class Gun extends Item{
    private Sound gunfire;
    private RenderComponent view;

    public Gun(){
        super("Gun");
        this.setView(generateView());
        gunfire = new Sound(Resource.getSoundData("45acp.ogg"));
    }

    @Override
    public RenderComponent generateView() {
        return new RenderComponent(new TextureRenderable(
                ObjectCreator.createQuadPrism(new Vector3f(-0.07f, -0.03f, -0.1f),
                        new Vector3f(0.07f, 0.1f, 0.9f)),
                Texture.ofColor(Color.BLACK)));
    }

    @Override
    public boolean use(){
        Bullet bullet = new Bullet(this);
        WorldEngine.getCurrent().attach(bullet);
        gunfire.rewind();
        gunfire.play();
        return false;
    }

}
