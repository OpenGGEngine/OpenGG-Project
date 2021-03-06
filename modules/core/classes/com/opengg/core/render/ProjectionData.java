/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.shader.CommonUniforms;
import com.opengg.core.render.window.WindowController;

/**
 *
 * @author Javier
 */
public class ProjectionData {
    public static final int PERSPECTIVE = 0,
            ORTHO = 1,
            CUSTOM = 3;
    
    int type = 0;
    
    float minz;
    float maxz;
    
    float fov;
    public float ratio;
    
    float miny;
    float maxy;
    float minx;
    float maxx;

    Matrix4f custom;
    
    private ProjectionData(){}
    
    public static ProjectionData getPerspective(float fov, float minz, float maxz){
        ProjectionData data = new ProjectionData();
        
        data.type = PERSPECTIVE;
        data.fov = fov;
        data.ratio = WindowController.getWindow().getRatio();
        data.minz = minz;
        data.maxz = maxz;
        
        return data;
    }

    public static ProjectionData getOrthographic(Vector3f p1, Vector3f p2){
        ProjectionData data = new ProjectionData();

        data.type = ORTHO;
        data.custom = Matrix4f.orthographic(p1.x, p2.x, p1.y, p2.y, p1.z, p2.z);

        return data;
    }

    public static ProjectionData getCustom(Matrix4f projection){
        ProjectionData data = new ProjectionData();

        data.custom = projection;
        data.type = CUSTOM;

        return data;
    }
    
    public void use(){
        ratio = WindowController.getWindow().getRatio();
        if(type == PERSPECTIVE)
            CommonUniforms.setPerspective(fov, ratio, minz, maxz);
        else if(type == CUSTOM || type == ORTHO)
            CommonUniforms.setProjection(custom);
    }

    public Matrix4f getMatrix(){
        return Matrix4f.perspective(fov, ratio, minz, maxz);
    }

}
