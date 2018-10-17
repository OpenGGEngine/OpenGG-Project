/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.light.Light;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;
import java.util.Map;

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
        onWorldChange(this::use);
    }

    public void use(){
        getWorld().getRenderEnvironment().addLight(getLight());
    }
    
    @Override
    public void update(float delta) {
        light.setPosition(getPosition());
    }    
    
    public Light getLight(){
        return light;
    }
    
    @Override
    public void serialize(GGOutputStream stream) throws IOException{
        super.serialize(stream);
        stream.write(light.getColor());
        stream.write(light.getDistance());
        stream.write(getWorld().getRenderEnvironment().getLights().contains(light));
    }
    
    @Override
    public void deserialize(GGInputStream stream) throws IOException{
        super.deserialize(stream);
        light = Light.createPoint(new Vector3f(), stream.readVector3f(), stream.readFloat());
        boolean use = stream.readBoolean();
        
        OpenGG.asyncExec(this::use);
    }

}
