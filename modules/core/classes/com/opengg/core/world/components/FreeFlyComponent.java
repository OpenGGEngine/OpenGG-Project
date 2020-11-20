/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

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
public class FreeFlyComponent extends ControlledComponent implements Actionable{
    private final ActionTransmitterComponent actionTransmitter;
    private final CameraComponent camera;

    private final Vector3fm control = new Vector3fm();
    private Vector3f currot = new Vector3f();
    private Vector3f vel = new Vector3f();

    private float rotspeed = 30;
    private float speed = 5;
    private final WorldObject head;
    
    public FreeFlyComponent(){
        actionTransmitter = new ActionTransmitterComponent();
        camera = new CameraComponent();
        head = new WorldObject();

        attach(actionTransmitter);
        attach(head);
        head.attach(camera);
    }
    
    @Override
    public void update(float delta){
        if(isCurrentUser()){
            Vector2f mousepos = getMouse();
            currot = new Vector3f(-mousepos.y, -mousepos.x, 0);
            this.setRotationOffset(Quaternionf.createYXZ(currot));

            vel = this.getRotation().transform(new Vector3f(control).multiply(delta * speed));
            setPositionOffset(getPositionOffset().add(vel));
        }else{
            this.setPositionOffset(this.getPositionOffset().add(vel));
        }
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
                case "fast":
                    speed = 50;
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
                case "fire":
                    System.exit(0);
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
                case "fast":
                    speed = 5;
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
    public void serializeUpdate(GGOutputStream stream) throws IOException{
        super.serializeUpdate(stream);
        stream.write(vel);
    }

    @Override
    public void deserializeUpdate(GGInputStream stream, float delta) throws IOException{
        super.deserializeUpdate(stream, delta);
        vel = stream.readVector3f();
        setPositionOffset(getPosition().add(vel.multiply(delta)));
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

        this.onWorldChange();
    }

    @Override
    public void onUserChange(){
        actionTransmitter.setUserId(getUserId());
        camera.setUserId(getUserId());
    }
}
