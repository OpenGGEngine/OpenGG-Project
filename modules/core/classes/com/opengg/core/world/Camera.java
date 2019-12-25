/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.View;

/**
 *
 * @author Javier
 */
public class Camera implements View {
    private Vector3f pos;
    private Quaternionf rot;

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
    
    public void setPosition(Vector3f posi){
        pos = posi;
    }
    
    public Vector3f getPosition(){
        return pos;
    }
    
    public void setRotation(Quaternionf rota){
        rot = rota;
    }

    @Override
    public Quaternionf getRotation(){
        return rot;
    }
    
    public Matrix4f getMatrix(){
        return new Matrix4f().translate(pos).rotate(rot).invert();
    }
}
