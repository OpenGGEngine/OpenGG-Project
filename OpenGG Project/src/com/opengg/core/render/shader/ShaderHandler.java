/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.shader;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.render.shader.premade.ShaderEnabled;
import com.opengg.core.world.Camera;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ShaderHandler {
    static List<ShaderEnabled> shaders = new ArrayList<>();
    
    static ShaderEnabled currentShader;
    
    public static void addShader(ShaderEnabled s){
        shaders.add(s);
    }
    
    public static void setCurrentShader(ShaderEnabled s){
        currentShader = s;
        s.use();
    }
    
    public static void setView(Camera c){
        Vector3f pos = c.getPos();
        Vector3f rot = c.getRot();       
        Matrix4f posm = Matrix4f.translate(pos.x, pos.y, pos.z);
        Matrix4f rotm = Matrix4f.rotate(rot.x,0,1,0);
        
        for(ShaderEnabled shader : shaders){
            shader.setView(rotm.multiply(posm));
        }
        
        currentShader.use();
    }
    public static void setPerspective(float fov, float aspect, float znear, float zfar){
        for(ShaderEnabled shader : shaders){
            shader.setProjection(fov, aspect, znear, zfar);
        }
        currentShader.use();
    }
    public static void setOrtho(float left, float right, float bottom, float top, float near, float far){
        for(ShaderEnabled shader : shaders){
            shader.setOrtho(left, right, bottom, top, near, far);
        }
        currentShader.use();
    }
    public static void setFrustum(float left, float right, float bottom, float top, float near, float far){
        for(ShaderEnabled shader : shaders){
            shader.setFrustum(left, right, bottom, top, near, far);
        }
        currentShader.use();
    }
    public static void setLightPos(Vector3f lightpos){
        for(ShaderEnabled shader : shaders){
            shader.setLightPos(lightpos);
        }
        currentShader.use();
    }
    public static void checkForErrors(){
        for(ShaderEnabled shader : shaders){
            shader.checkError();
        }
        currentShader.use();
    }
    
}
