/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Warren
 */
public class SunComponent extends RenderComponent{
    int SUN_DIS = 800;
    float rotspeed;
    float currot;
    Texture texture;
    LightComponent light;
    
    public SunComponent(Texture texture, float scale, float rotspeed){
        super();
        Buffer[] buffers = ObjectCreator.createSquareBuffers(new Vector2f(0,0), new Vector2f(1,1), 0f);
        setDrawable(new TexturedDrawnObject((FloatBuffer)buffers[0], (IntBuffer)buffers[1], texture));
        this.rotspeed = rotspeed;
        this.texture = texture;
        this.scale = new Vector3f(scale);
        light = new LightComponent(new Light(new Vector3f(), new Vector3f(1,0.3f,0.3f),100000,10000));
        light.use();
        this.attach(light);
        this.setShader("texture");
    }
    
    @Override
    public void render() {
        ShaderController.setBillBoard(1);
        texture.useTexture(0);
        super.render();
        ShaderController.setBillBoard(0);
    }
    
    @Override
    public void update(float delta){
        currot += delta * rotspeed;
        pos.x = (float) (SUN_DIS * Math.cos(currot));
        pos.y = (float) (SUN_DIS * Math.sin(currot));
    }
}
