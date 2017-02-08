/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.World;

/**
 *
 * @author Javier
 */
public class WorldObject extends ComponentHolder{
    
    public WorldObject(Vector3f pos, Quaternionf rot, World thisWorld) {
        
        this.pos = pos;
        this.rot = rot;
    }

    public WorldObject() {
        super();
        pos = new Vector3f(0, 0, 0);  
        rot = new Quaternionf();
    }

    @Override
    public void attach(Component c) {
        c.setParentInfo(this);
        super.attach(c);
    }
}
