/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.light.Light;

/**
 *
 * @author Javier
 */
public class LightComponent extends Component{
    Light l;
    
    public LightComponent(){
        super();
        l = new Light(new Vector3f(0,0,0),new Vector3f(0,0,0),0,0);
    }
    
    public LightComponent(Light l){
        super();
        this.l = l;
    }

    @Override
    public void update(float delta) {
        l.pos = parent.getPosition();
    }    
}
