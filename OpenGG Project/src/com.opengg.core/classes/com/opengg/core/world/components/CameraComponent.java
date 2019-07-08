/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.GGInfo;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.world.Camera;

/**
 *
 * @author Javier
 */
public class CameraComponent extends ControlledComponent{
    public Camera camera;

    public CameraComponent(){
        camera = new Camera();
    }

    @Override
    public void onEnable(){
        if(isCurrentUser())
            RenderEngine.useView(camera);
    }

    @Override
    public void onWorldMadePrimary(){
        if(isCurrentUser())
            RenderEngine.useView(camera);
    }

    @Override
    public void onUserChange(){
        if(isCurrentUser())
            RenderEngine.useView(camera);
    }

    @Override
    public void update(float delta){
        camera.setPosition(getPosition());
        camera.setRotation(getRotation());
    }
}
