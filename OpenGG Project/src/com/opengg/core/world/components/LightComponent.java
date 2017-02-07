/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.render.light.Light;

/**
 *
 * @author Javier
 */
public class LightComponent extends Component{
    Light l;
    
    public LightComponent(Light l){
        this.l = l;
    }

    @Override
    public void update(float delta) {
        l.pos = parent.getPosition();
    }    
}
