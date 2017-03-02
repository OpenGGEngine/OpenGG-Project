/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class Camera {
    Vector3f pos;
    Quaternionf rot;
    public Camera(){
        this.pos = new Vector3f();
        this.rot = new Quaternionf();
    }
    /**
     * 
     * Creates a Camera
     * @param pos Camera Position
     * @param rot Camera Rotation
     */
    public Camera(Vector3f pos, Quaternionf rot){
        this.pos = pos;
        this.rot = rot;
    }
    
    public void setPos(Vector3f posi){
        pos = posi;
    }
    
    public Vector3f getPos(){
        return pos;
    }
    
    public void setRot(Quaternionf rota){
        rot = rota;
    }
    
    public Quaternionf getRot(){
        return rot;
    }
    
    public Matrix4f getMatrix(){
        Matrix4f matrix = new Matrix4f().rotateQuat(rot).translate(pos);
        return matrix;
    }
}
