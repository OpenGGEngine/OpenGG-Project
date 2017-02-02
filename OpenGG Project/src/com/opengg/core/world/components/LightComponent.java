/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.light.Light;

/**
 *
 * @author Javier
 */
public class LightComponent implements Positioned, Updatable{

    Light l;
    Component parent;
    Vector3f offset;
    
    public LightComponent(Light l){
        this.l = l;
    }
    
    @Override
    public void setPosition(Vector3f pos) {
        offset = pos;
    }

    @Override
    public Vector3f getPosition() {
        return offset;
    }

    @Override
    public void setParentInfo(Component parent) {
        this.parent = parent;
    }

    @Override
    public void update(float delta) {
        if(parent instanceof Positioned){
            l.pos = ((Positioned)parent).getPosition().add(offset);
        }
    }    
}
