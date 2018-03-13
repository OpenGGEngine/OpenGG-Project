/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Warren
 */
public class SunComponent extends RenderComponent{
    int SUN_DIS = 5000;
    float rotspeed;
    float currot;
    Texture texture;
    LightComponent light;

    public SunComponent(){
        this(TextureManager.getDefault(), 0.01f);
    }
    
    public SunComponent(TextureData texture){
        this(texture, 0.01f);
    }
    
    public SunComponent(TextureData texture, float rotspeed){
        super();
        OpenGG.asyncExec(() -> {
            Drawable drawn = ObjectCreator.createSquare(new Vector2f(0,0), new Vector2f(1,1), 0f);
            setDrawable(new TexturedDrawnObject(drawn, Texture.get2DSRGBTexture(texture)));
        });
        
        this.rotspeed = rotspeed;
        light = new LightComponent(new Light(new Vector3f(), new Vector3f(1,0.3f,0.3f),100000,10000));
        light.use();
        this.attach(light);
        this.setShader("texture");
    }
    
    @Override
    public void render() {
        ShaderController.setBillBoard(1);
        texture.use(0);
        super.render();
        ShaderController.setBillBoard(0);
    }
    
    @Override
    public void update(float delta){
        currot += delta * rotspeed;
        setPositionOffset(getPositionOffset().setX((float) (SUN_DIS * Math.cos(currot))));
        getPositionOffset().setY((float) (SUN_DIS * Math.sin(currot)));
    }
    
    public float getRotationSpeed() {
        return rotspeed;
    }

    public void setRotationSpeed(float rotspeed) {
        this.rotspeed = rotspeed;
    }

    public float getCurrentRotation() {
        return currot;
    }

    public void setCurrentRotation(float currot) {
        this.currot = currot;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    @Override
    public void serialize(GGOutputStream stream) throws IOException{
        super.serialize(stream);
        stream.write((int)SUN_DIS);
        stream.write((float)rotspeed);
        stream.write((float)currot);
        stream.write(texture.getData().get(0).source);
    }
    
    @Override
    public void deserialize(GGInputStream stream) throws IOException{
        super.deserialize(stream);
        SUN_DIS = stream.readInt();
        rotspeed = stream.readFloat();
        currot = stream.readFloat();
        texture = Texture.get2DTexture(stream.readString());
    }
}
