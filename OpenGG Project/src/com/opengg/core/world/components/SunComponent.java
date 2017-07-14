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
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.util.GGByteInputStream;
import com.opengg.core.util.GGByteOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
        this(Texture.get2DTexture(TextureManager.getDefault()), 0.01f);
    }
    
    public SunComponent(Texture texture){
        this(texture, 0.01f);
    }
    
    public SunComponent(Texture texture, float rotspeed){
        super();
        Buffer[] buffers = ObjectCreator.createSquareBuffers(new Vector2f(0,0), new Vector2f(1,1), 0f);
        setDrawable(new TexturedDrawnObject((FloatBuffer)buffers[0], (IntBuffer)buffers[1], texture));
        this.rotspeed = rotspeed;
        this.texture = texture;
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
        pos.x = (float) (SUN_DIS * Math.cos(currot));
        pos.y = (float) (SUN_DIS * Math.sin(currot));
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
    public void serialize(GGByteOutputStream stream) throws IOException{
        super.serialize(stream);
        stream.write((int)SUN_DIS);
        stream.write((float)rotspeed);
        stream.write((float)currot);
        stream.write(texture.getData().get(0).source);
    }
    
    @Override
    public void deserialize(GGByteInputStream stream) throws IOException{
        super.deserialize(stream);
        SUN_DIS = stream.readInt();
        rotspeed = stream.readFloat();
        currot = stream.readFloat();
        texture = Texture.get2DTexture(stream.readString());
    }
}
