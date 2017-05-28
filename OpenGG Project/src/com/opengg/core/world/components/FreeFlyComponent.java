/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.BindController;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Actionable;

/**
 *
 * @author Javier
 */
public class FreeFlyComponent extends Component implements Actionable{
    private UserControlComponent pcontrol;
    private CameraComponent view;
    
    Vector3f control = new Vector3f();
    Vector3f controlrot = new Vector3f();
    Vector3f currot = new Vector3f();
    float rotspeed = 30;
    float speed = 30;
    
    public FreeFlyComponent(){
        pcontrol = new UserControlComponent();
        view = new CameraComponent();
        attach(pcontrol);
        attach(view);
    }
    
    @Override
    public void update(float delta){
        currot.x += controlrot.x * rotspeed * delta;
        currot.y += controlrot.y * rotspeed * delta;
        currot.z += controlrot.z * rotspeed * delta;
        this.setRotationOffset(new Quaternionf(currot));   
        
        Vector3f nvector = control.multiply(delta * 15);
        nvector = this.getRotation().transform(nvector);
        this.pos.addThis(nvector);
    }
    
    @Override
    public void onAction(Action action) {
        if(action.type == ActionType.PRESS){
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
        }else{
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
        }
    }
    
    public void use(){
        BindController.setOnlyController(pcontrol);
        view.use();
    }
}
