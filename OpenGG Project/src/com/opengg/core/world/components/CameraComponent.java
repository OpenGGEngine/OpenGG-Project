/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.world.Camera;

/**
 *
 * @author Javier
 */
public class CameraComponent extends Component{
    public Camera camera;

    public CameraComponent(){
        camera = new Camera();
    }
    
    public void use(){
        RenderEngine.useCamera(camera);
    }
    
    @Override
    public void update(float delta){
        camera.setPos(getPosition().inverse());
        camera.setRot(getRotation().invert());
    }
}
