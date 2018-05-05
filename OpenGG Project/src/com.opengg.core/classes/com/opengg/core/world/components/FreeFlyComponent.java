/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.Configuration;
import com.opengg.core.engine.BindController;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.io.input.mouse.MouseMoveListener;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector3fm;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Actionable;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class FreeFlyComponent extends ControlledComponent implements Actionable, MouseMoveListener{
    private ActionTransmitterComponent pcontrol;
    private CameraComponent view;
    private int userid = 0;
    
    Vector3fm control = new Vector3fm();
    Vector2f controlrot = new Vector2f();
    Vector3f currot = new Vector3f();
    float rotspeed = 30;
    float speed = 30;
    private final WorldObject head;
    
    public FreeFlyComponent(){
        pcontrol = new ActionTransmitterComponent();
        view = new CameraComponent();
        head = new WorldObject();
        attach(pcontrol);
        attach(head);
        head.attach(view);
    }
    
    @Override
    public void update(float delta){
        Vector2f mousepos = MouseController.get();
        float mult = Configuration.getFloat("sensitivity");
        currot = new Vector3f(mousepos.multiply(mult).y, mousepos.multiply(mult).x, 0);
        this.setRotationOffset(new Quaternionf(new Vector3f(0, currot.y, currot.z)));
        head.setRotationOffset(new Quaternionf(new Vector3f(currot.x,0,0)));
       
        Vector3f nvector = new Vector3f(control).multiply(delta * 15);
        nvector = this.getRotation().transform(nvector);
        setPositionOffset(getPositionOffset().add(nvector));
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
                    //controlrot.y -= 1;
                    break;
                case "lookleft":
                    //controlrot.y += 1;
                    break;
                 case "lookup":
                    //controlrot.x += 1;
                    break;
                case "lookdown":
                    //controlrot.x -= 1;
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
                    //controlrot.y += 1;
                    break;
                case "lookleft":
                    //controlrot.y -= 1;
                    break;
                case "lookup":
                    //controlrot.x -= 1;
                    break;
                case "lookdown":
                    //controlrot.x += 1;
                    break;

            }
        }
    }

    @Override
    public void use(){
        BindController.setOnlyController(pcontrol);
        view.use();
    }
    
    @Override
    public void serialize(GGOutputStream stream) throws IOException{
        super.serialize(stream);
        stream.write(rotspeed);
        stream.write(speed);
    }
    
    @Override
    public void deserialize(GGInputStream stream) throws IOException{
        super.deserialize(stream);
        rotspeed = stream.readFloat();
        speed = stream.readFloat();
        useComponent();
    }

    @Override
    public void onMove(Vector2f pos){
        this.controlrot = pos;
    }
}
