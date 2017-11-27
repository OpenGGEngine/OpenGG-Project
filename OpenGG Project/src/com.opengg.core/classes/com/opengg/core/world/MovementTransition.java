/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;

/**
 *
 * @author Javier
 */
public class MovementTransition extends Transition{
    public static final int LINEAR_RELATIVE = 1,
                            LINEAR_ABSOLUTE = 2;
    
    public Vector3f initPos;
    public Vector3f finalPos;
    
    public MovementTransition(Component c, Vector3f end, float time, int type){
        this.comp = c;
        this.initPos = c.getPositionOffset();
        this.duration = time;
        if(type == LINEAR_ABSOLUTE)
            this.finalPos = end;
        else
            this.finalPos = this.initPos.add(end);
    }

    @Override
    public void update(float delta) {
        this.comp.setPositionOffset(Vector3f.lerp(initPos, finalPos, this.elapsedTime()/this.duration()));
    }

}
