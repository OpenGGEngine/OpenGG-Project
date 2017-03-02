/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Actionable;

/**
 *
 * @author Javier
 */
public class PlayerComponent extends ComponentHolder implements Actionable{
    Vector3f control = new Vector3f();
    Vector3f controlrot = new Vector3f();
    Vector3f currot = new Vector3f();
    float speed = 100;
    float rotspeed = 3;
    
    @Override
    public void update(float delta){
        float deltasec = delta / 1000;
        
        currot.x += controlrot.x  * rotspeed * deltasec;
        currot.y += controlrot.y  * rotspeed * deltasec;
        currot.z += controlrot.z  * rotspeed * deltasec;
        
        this.setRotationOffset(new Quaternionf(currot));
        
        this.pos.x += control.x * speed * deltasec;
        this.pos.y += control.y * speed * deltasec;
        this.pos.z += control.z * speed * deltasec;
    }
    
    @Override
    public void onAction(Action action) {
        if(action.type == ActionType.PRESS){
            switch(action.name){
                case "forward":
                    control.z += 1;
                    break;
                case "backward":
                    control.z -= 1;
                    break;
                case "left":
                    control.x += 1;
                    break;
                case "right":
                    control.x -= 1;
                    break;
                case "up":
                    control.y -= 1;
                    break;
                case "down":
                    control.y += 1;
                    break;
                case "lookright":
                    controlrot.y -= 1;
                    break;
                case "lookleft":
                    controlrot.y += 1;
                    break;
                 case "lookup":
                    controlrot.x -= 1;
                    break;
                case "lookdown":
                    controlrot.x += 1;
                    break;
            }
        }else{
            switch(action.name){
                case "forward":
                    control.z -= 1;
                    break;
                case "backward":
                    control.z += 1;
                    break;
                case "left":
                    control.x -= 1;
                    break;
                case "right":
                    control.x += 1;
                    break;
                case "up":
                    control.y += 1;
                    break;
                case "down":
                    control.y -= 1;
                    break;
                case "lookright":
                    controlrot.y += 1;
                    break;
                case "lookleft":
                    controlrot.y -= 1;
                    break;
                case "lookup":
                    controlrot.x += 1;
                    break;
                case "lookdown":
                    controlrot.x -= 1;
                    break;
            }
        }
    }
    
}
