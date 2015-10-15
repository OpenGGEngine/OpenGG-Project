/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.render.shader.ShaderProgram;

/**
 *
 * @author Javier
 */
public class Camera {
    ShaderProgram s;
    Vector3f pos;
    Vector3f rot;
    int uniView;
    public Camera(ShaderProgram s, Vector3f pos, Vector3f rot){
        this.s = s;
        this.pos = pos;
        this.rot = rot;
        int i = 1;
        uniView = s.getUniformLocation("view");
    }
    public void use(){
        Matrix4f posm = Matrix4f.translate(pos.x, pos.y, pos.z);
        Matrix4f rotm = Matrix4f.rotate(rot.x,0,1,0);
        s.setUniform(uniView, posm.multiply(rotm));  
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
