/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.test;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.objects.ObjectBuffers;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.world.components.LightComponent;
import com.opengg.core.world.components.RenderComponent;

/**
 *
 * @author Warren
 */
public class SunComponent extends RenderComponent{
    Sun sun;
    Light l;
    public SunComponent(Sun sun){
        super(new TexturedDrawnObject(ObjectBuffers.getSquareUI(0, 1, 0,1, 1, 1, false),1,sun.getTexture()));
        this.sun = sun;
        this.scale = new Vector3f(sun.getScale(),sun.getScale(),sun.getScale());
       l = new Light(sun.getWorldPosition(RenderEngine.camera.getPos().inverse()), new Vector3f(1,0.3f,0.3f),100000,10000);
       LightComponent loser = new LightComponent(l);
       this.attach(loser);
    }
    @Override
    public void render() {
        sun.getTexture().useTexture(0);
        super.render();
    }
    @Override
    public void update(float delta){
        
        this.setRotationOffset(RenderEngine.camera.getRot().invert());
        this.setPositionOffset(sun.getWorldPosition(RenderEngine.camera.getPos().inverse()));
        super.update(delta);
    }
}
