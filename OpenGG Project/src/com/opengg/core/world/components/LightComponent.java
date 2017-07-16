/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.light.Light;
import com.opengg.core.util.GGByteInputStream;
import com.opengg.core.util.GGByteOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class LightComponent extends Component{
    Light l;
    
    public LightComponent(){
        super();
        l = new Light(new Vector3f(0,0,0),new Vector3f(1,1,1),100,0);
        use();
    }
    
    public LightComponent(Light l){
        super();
        this.l = l;
    }

    public void use(){
        RenderEngine.addLight(l);
    }
    
    @Override
    public void update(float delta) {
        l.setPosition(getPosition());
    }    
    
    public Light getLight(){
        return l;
    }
    
    @Override
    public void serialize(GGByteOutputStream stream) throws IOException{
        super.serialize(stream);
        stream.write(l.getColor());
        stream.write(l.getDistance());
        stream.write(RenderEngine.getLights().contains(l));
    }
    
    @Override
    public void deserialize(GGByteInputStream stream) throws IOException{
        super.deserialize(stream);
        l = new Light(new Vector3f(), stream.readVector3f(), stream.readFloat(), 0);
        boolean use = stream.readBoolean();
        
        OpenGG.addExecutable(() -> {
            if(use) use();
        });
    }
}
