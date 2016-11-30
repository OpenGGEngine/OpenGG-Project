/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.Vector3f;
import com.opengg.core.io.input.KeySet;
import com.opengg.core.io.input.KeyboardListener;
import com.opengg.core.util.GlobalInfo;
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
        s = GlobalInfo.s;
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