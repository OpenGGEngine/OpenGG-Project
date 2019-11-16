/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class WaterComponent extends RenderComponent{
    private float movespeed;
    private float current;
    private float textureScale;
    private float size;
    private TextureData texture;
    
    public WaterComponent(){}
    
    public WaterComponent(TextureData tex, float size){
        this(tex, 0.1f, 100, size);
    }
    
    public WaterComponent(TextureData texture, float movespeed, float textureScale, float size){
        OpenGG.asyncExec(() -> {
            Renderable drawn = ObjectCreator.createSquare(new Vector2f(-size,-size), new Vector2f(size,size), 0);
            setRenderable(new TextureRenderable(drawn, Texture.get2DSRGBTexture(texture)));
        });
        
        this.texture = texture;
        this.size = size;
        this.textureScale = textureScale;
        this.movespeed = movespeed;
        this.setRotationOffset(Quaternionf.createXYZ(new Vector3f(90,0,0)));
        this.setShader("water");
    }
    
    @Override
    public void update(float delta){
        current += delta * movespeed;
    }
    
    @Override
    public void render(){
        ShaderController.setUVCoordinateMultiplierX(textureScale);
        ShaderController.setUniform("uvoffsetx", FastMath.sin(current)/4);
        ShaderController.setUniform("uvoffsety", FastMath.sin(current)/4);
        if(RenderEngine.getSkybox() == null) return;
        RenderEngine.getSkybox().getCubemap().use(2);
        super.render();
        ShaderController.setUniform("uvoffsetx", 0f);
        ShaderController.setUniform("uvoffsety", 0f);
        ShaderController.setUVCoordinateMultiplierX(1);
    }
    
    public float getMovespeed() {
        return movespeed;
    }

    public void setMovespeed(float movespeed) {
        this.movespeed = movespeed;
    }

    public float getTextureScale() {
        return textureScale;
    }

    public void setTextureScale(float textureScale) {
        this.textureScale = textureScale;
    }
    
    public TextureData getTexture() {
        return texture;
    }

    public void setTexture(TextureData texture) {
        this.getRenderable();
    }
    
    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(size);
        out.write(movespeed);
        out.write(current);
        out.write(textureScale);
        out.write(texture.source);
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        size = in.readFloat();
        movespeed = in.readFloat();
        current = in.readFloat();
        textureScale = in.readFloat();
        texture = Resource.getTextureData(in.readString());
        OpenGG.asyncExec(() -> {
            Renderable drawn = ObjectCreator.createSquare(new Vector2f(-size,-size), new Vector2f(size,size), 0);
            setRenderable(new TextureRenderable(drawn, Texture.get2DSRGBTexture(texture)));
        });
    }

    @Override
    public void serializeUpdate(GGOutputStream out) throws IOException{
        super.serializeUpdate(out);
        out.write(current);
    }

    @Override
    public void deserializeUpdate(GGInputStream in, float delta) throws IOException{
        super.deserializeUpdate(in, delta);
        current = in.readFloat();
    }
}
