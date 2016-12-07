/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class Camera {
    Vector3f pos;
    Vector3f rot;
    int uniView;
    public Camera(){
        this.pos = new Vector3f();
        this.rot = new Vector3f();
    }
    /**
     * 
     * Creates a Camera
     * @param pos Camera Position
     * @param rot Camera Rotation
     */
    public Camera(Vector3f pos, Vector3f rot){
        this.pos = pos;
        this.rot = rot;
    }
    public void setPos(Vector3f posi){
        pos = posi;
    }
    public Vector3f getPos(){
        return pos;
    }
    public void setRot(Vector3f rota){
        rot = rota;
    }
    public Vector3f getRot(){
        return rot;
    }
}
