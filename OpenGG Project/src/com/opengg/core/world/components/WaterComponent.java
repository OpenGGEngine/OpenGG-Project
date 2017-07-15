/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.util.GGByteInputStream;
import com.opengg.core.util.GGByteOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class WaterComponent extends RenderComponent{
    float movespeed;
    float current;
    float tscale;
    float size;
    Texture texture;
    
    public WaterComponent(){}
    
    public WaterComponent(Texture tex, float size){
        this(tex, 0.1f, 100, size);
    }
    
    public WaterComponent(Texture texture, float movespeed, float tscale, float size){
        super(ObjectCreator.createSquare(new Vector2f(-size,-size), new Vector2f(size,size), 0));
        this.size = size;
        this.texture = texture;
        this.tscale = tscale;
        this.movespeed = movespeed;
        this.setRotationOffset(new Quaternionf(new Vector3f(90,0,0)));
        this.setShader("water");
    }
    
    @Override
    public void update(float delta){
        current += delta * movespeed;
    }
    
    @Override
    public void render(){
        texture.use(0);
        ShaderController.setUVMultX(tscale);
        ShaderController.setUniform("uvoffsetx", FastMath.sin(current)/4);
        ShaderController.setUniform("uvoffsety", FastMath.sin(current)/4);
        RenderEngine.getSkybox().getCubemap().use(2);
        super.render();
        ShaderController.setUniform("uvoffsetx", 0f);
        ShaderController.setUniform("uvoffsety", 0f);
        ShaderController.setUVMultX(1);
    }
    
    public float getMovespeed() {
        return movespeed;
    }

    public void setMovespeed(float movespeed) {
        this.movespeed = movespeed;
    }

    public float getTscale() {
        return tscale;
    }

    public void setTscale(float tscale) {
        this.tscale = tscale;
    }
    
    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    @Override
    public void serialize(GGByteOutputStream out) throws IOException{
        super.serialize(out);
        out.write(size);
        out.write(movespeed);
        out.write(current);
        out.write(tscale);
        out.write(texture.getData().get(0).source);
    }
    
    @Override
    public void deserialize(GGByteInputStream in) throws IOException{
        super.deserialize(in);
        size = in.readFloat();
        movespeed = in.readFloat();
        current = in.readFloat();
        tscale = in.readFloat();
        texture = Texture.get2DTexture(TextureManager.loadTexture(in.readString()));
        OpenGG.addExecutable(() -> {
            super.setDrawable(ObjectCreator.createSquare(new Vector2f(-size,-size), new Vector2f(size,size), 0));
        });
    }
}
