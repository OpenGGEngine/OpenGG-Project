/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.math.Vector3f;
import com.opengg.core.io.input.keyboard.KeySet;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.engine.EngineInfo;
import com.opengg.core.world.components.Updatable;
import com.opengg.core.world.components.physics.PhysicsComponent;

/**
 *
 * @author Javier
 */
public class PlayerController extends WorldObject implements Updatable {
    KeySet s;
    PhysicsComponent c;
    Vector3f acc;

    public PlayerController(){
        s = EngineInfo.s;
    }
    
    public void setPlayerPhysics(PhysicsComponent c){
        this.c = c;
        this.attach(c);
    }
    
    @Override
    public void update(float delta){
        c.acceleration = acc;
    }  
}
