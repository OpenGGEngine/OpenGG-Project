/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.shader;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.render.shader.premade.DefaultDrawnShader;
import com.opengg.core.world.Camera;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ShaderHandler {
    static List<DefaultDrawnShader> shaders = new ArrayList<>();
    
    public static void addShader(DefaultDrawnShader s){
        shaders.add(s);
    }
    
    public static void setView(Camera c){
        Vector3f pos = c.getPos();
        Vector3f rot = c.getRot();       
        Matrix4f posm = Matrix4f.translate(pos.x, pos.y, pos.z);
        Matrix4f rotm = Matrix4f.rotate(rot.x,0,1,0);
        for(DefaultDrawnShader shader : shaders){
            shader.setView(rotm.multiply(posm));
        }
    }
    public static void setPerspective(float fov, float aspect, float znear, float zfar){
        for(DefaultDrawnShader shader : shaders){
            shader.setProjection(fov, aspect, znear, zfar);
        }
    }
    public static void setOrtho(float left, float right, float bottom, float top, float near, float far){
        for(DefaultDrawnShader shader : shaders){
            shader.setOrtho(left, right, bottom, top, near, far);
        }
    }
    public static void setLightPos(Vector3f lightpos){
        for(DefaultDrawnShader shader : shaders){
            shader.setLightPos(lightpos);
        }
    }
    
}
