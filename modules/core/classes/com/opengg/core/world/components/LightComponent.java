/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.light.Light;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class LightComponent extends Component{
    private Light light;
    
    public LightComponent(){
        this(Light.createPoint(new Vector3f(0,0,0),new Vector3f(1,1,1),100));
    }
    
    public LightComponent(Light light){
        super();
        this.light = light;
        this.setPositionOffset(light.getPosition());
    }

    public Light getLight(){
        return light;
    }

    @Override
    public void onWorldChange(){
        getWorld().getRenderEnvironment().addLight(getLight());
    }

    @Override
    public void onEnable(){
        this.light.setActive(true);
    }

    @Override
    public void onDisable() {
        this.light.setActive(false);
    }

    @Override
    public void finalizeComponent() {
        getWorld().getRenderEnvironment().removeLight(getLight());
    }

    @Override
    public void onPositionChange(Vector3f npos) {
        light.setPosition(npos);
    }

    @Override
    public void onRotationChange(Quaternionf nrot) {
        light.setRotation(nrot);
    }

    @Override
    public void serialize(GGOutputStream stream) throws IOException{
        super.serialize(stream);
        light.serialize(stream);
    }
    
    @Override
    public void deserialize(GGInputStream stream) throws IOException{
        super.deserialize(stream);
        OpenGG.onMainThread(() -> {
            try {
                light = Light.createFromStream(stream);
                this.setPositionOffset(light.getPosition());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
