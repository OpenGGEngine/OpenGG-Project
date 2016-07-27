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
import com.opengg.core.world.components.PhysicsComponent;

/**
 *
 * @author Javier
 */
public class Player extends WorldObject implements KeyboardListener {
    KeySet s;
    PhysicsComponent c;
    Vector3f acc;

    public Player(){
        s = GlobalInfo.s;
    }
    
    public void setPlayerPhysics(PhysicsComponent c){
        this.c = c;
        this.attach(c);
    }
    
    @Override
    public void update(float delta){
        c.acceleration = acc;
        super.update(delta);
    }
    
    @Override
    public void keyPressed(int key) {
        
    }

    @Override
    public void keyReleased(int key) {
        
    }
    
}
