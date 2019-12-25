/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.test.network.components;

import com.opengg.core.GGInfo;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.*;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Actionable;
import com.opengg.core.world.components.*;
import com.opengg.core.world.components.physics.RigidBodyComponent;
import com.opengg.test.network.OpenGGNetworkTest;
import com.opengg.test.network.OpenGGTestServer;

import java.io.IOException;

/**
 *
 * @author Javier
 */
public class Player extends Character implements Actionable{
    private CameraComponent camera;
    private ActionTransmitterComponent controller;
    private Vector3fm control = new Vector3fm();
    private boolean fps = false;

    public Player(){
        super();
        if(!GGInfo.isServer()) return;

        controller = new ActionTransmitterComponent();
        controller.setName("controller");

        camera = new CameraComponent();
        camera.setName("camera");
        attach(controller);
        head.attach(camera.setPositionOffset(new Vector3f(0, 0.4f, 1f)));
    }

    @Override
    public void update(float delta){
        if(GGInfo.isServer() || controller.getUserId() == GGInfo.getUserId()){
            Vector2f mousepos = controller.getMouse();

            movementDir = new Vector3f(control.x, 0 ,control.z);
            rotationDir = new Vector3f(-mousepos.y%360, -mousepos.x%360, 0);
        }

        super.update(delta);
    }

    @Override
    public void onDeath(){
        OpenGG.asyncExec(() -> {
            this.delete();
            OpenGGTestServer.createPlayer(controller.getUserId());
        });
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
                    jump = true;
                    break;
                case "use":
                    if(getCurrentItem().isPresent() && GGInfo.isServer()){
                        var remove = getCurrentItem().get().use();
                        if(remove){
                            this.removeItem(false);
                        }
                    }
                    break;
                case "drop":
                    this.removeItem(true);
                    break;
                case "test":
                    this.useItem(new Gun());
                    break;
                case "next":
                    if(getItems().isEmpty()) return;
                    this.useItem(getItems().get(0));
                    break;
                case "previous":
                    if(getItems().isEmpty()) return;
                    this.useItem(getItems().get(getItems().size()-1));
                    break;
                case "fps":
                    fps = !fps;
                    if(fps){
                        camera.setPositionOffset(new Vector3f(0, 0.2f, -0.1f));
                    }else{
                        camera.setPositionOffset(new Vector3f(0, 0.4f, 1f));
                    }
                    break;
            }
        }else{
            switch (action.name) {
                case "forward" -> control.z += 1;
                case "backward" -> control.z -= 1;
                case "left" -> control.x += 1;
                case "right" -> control.x -= 1;
            }
        }
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        OpenGG.asyncExec(() -> {
            camera = (CameraComponent) findByName("camera").get(0);
            controller = (ActionTransmitterComponent) findByName("controller").get(0);
        });
    }

    public void setUserID(int id){
        camera.setUserId(id);
        controller.setUserId(id);
        this.setName(NetworkEngine.getServer().getClientByID(id).get().getName() + id);
    }
}
